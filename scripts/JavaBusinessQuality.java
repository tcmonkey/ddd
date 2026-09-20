import com.sun.source.tree.*;
import com.sun.source.util.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import javax.tools.*;

/**
 * JDK17源代码质量门禁：业务流程编号覆盖与方法规模，语义设计仍需人工复核。
 *
 * @author AIGenerator
 */
public final class JavaBusinessQuality {
    private static final Pattern STEP = Pattern.compile("(?m)^\\s*//\\s*(\\d+)[.、]\\s*(\\S.*)$");
    private static final int MAX_STATEMENTS = 45;
    private static int methods, flows, failures;
    private static final List<String> rows = new ArrayList<>();

    public static void main(String[] arguments) throws Exception {
        if (!(arguments.length == 1
                || (arguments.length == 3 && arguments[1].equals("--report")))) {
            throw new IllegalArgumentException(
                    "Usage: java JavaBusinessQuality.java <root> [--report <csv-path>]");
        }
        // 1. 收集聚合根的手写业务源码，构建和依赖生成目录不参与扫描。
        Path root = Path.of(arguments[0]).toAbsolutePath().normalize();
        List<Path> files;
        try (var paths = Files.walk(root)) {
            files =
                    paths.filter(Files::isRegularFile)
                            .filter(
                                    p ->
                                            p.toString()
                                                    .replace('\\', '/')
                                                    .contains("/src/main/java/"))
                            .filter(p -> p.toString().endsWith(".java"))
                            .filter(
                                    p ->
                                            !Set.of("target", ".git", "node_modules", "dist")
                                                    .stream()
                                                    .anyMatch(
                                                            part ->
                                                                    Arrays.asList(
                                                                                    root.relativize(
                                                                                                    p)
                                                                                            .toString()
                                                                                            .split(
                                                                                                    "[/\\\\]"))
                                                                            .contains(part)))
                            .sorted()
                            .toList();
        }
        // 2. 仅解析语法树，无需加载业务依赖或执行应用代码。
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new IllegalStateException("QUALITY-JDK: run with JDK17 or newer");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (var manager =
                compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            JavacTask task =
                    (JavacTask)
                            compiler.getTask(
                                    null,
                                    manager,
                                    diagnostics,
                                    List.of("-proc:none", "--release", "17"),
                                    null,
                                    manager.getJavaFileObjectsFromPaths(files));
            Trees trees = Trees.instance(task);
            rows.add("file,method,line,statements,flow_blocks,classification");
            for (CompilationUnitTree unit : task.parse()) {
                String source = unit.getSourceFile().getCharContent(true).toString();
                Path path = Path.of(unit.getSourceFile().toUri());
                SourcePositions positions = trees.getSourcePositions();
                new TreeScanner<Void, Void>() {
                    String current = "initializer";
                    int methodFlows;
                    final Set<BlockTree> checked =
                            Collections.newSetFromMap(new IdentityHashMap<>());

                    @Override
                    public Void visitClass(ClassTree type, Void ignored) {
                        String packageName = unit.getPackageName().toString();
                        String typeName = type.getSimpleName().toString();
                        boolean inputAdapter =
                                packageName.matches(".*\\.adaptor\\..+\\.input(\\..*)?");
                        boolean misplacedController =
                                inputAdapter
                                        && typeName.endsWith("Controller")
                                        && !packageName.matches(
                                                ".*\\.input\\.controller(\\..*)?");
                        boolean misplacedAssembler =
                                inputAdapter
                                        && typeName.endsWith("Assembler")
                                        && !packageName.matches(
                                                ".*\\.input\\.assembler(\\..*)?");
                        if (misplacedController || misplacedAssembler) {
                            fail(
                                    "QUALITY-PACKAGE",
                                    unit.getLineMap()
                                            .getLineNumber(
                                                    positions.getStartPosition(unit, type)),
                                    "input Controller and Assembler must use sibling"
                                            + " input.controller/input.assembler packages");
                        }
                        return super.visitClass(type, ignored);
                    }

                    @Override
                    public Void visitMethod(MethodTree method, Void ignored) {
                        if (method.getBody() == null) return null;
                        methods++;
                        String previous = current;
                        int previousFlows = methodFlows;
                        current = method.getName().toString();
                        methodFlows = 0;
                        int statements = count(method.getBody());
                        long line =
                                unit.getLineMap()
                                        .getLineNumber(positions.getStartPosition(unit, method));
                        if (!current.equals("<init>")) {
                            check(method.getBody(), "method");
                            if (statements > MAX_STATEMENTS)
                                fail(
                                        "QUALITY-SIZE",
                                        line,
                                        "method has "
                                                + statements
                                                + " statements; separate responsibilities (limit "
                                                + MAX_STATEMENTS
                                                + ")");
                        }
                        boolean scheduled =
                                method.getModifiers().getAnnotations().stream()
                                        .anyMatch(
                                                annotation ->
                                                        annotation
                                                                .getAnnotationType()
                                                                .toString()
                                                                .endsWith("Scheduled"));
                        if (scheduled
                                && (!unit.getPackageName().toString().contains(".adaptor.")
                                        || !unit.getPackageName()
                                                .toString()
                                                .contains(".scheduler"))) {
                            fail(
                                    "QUALITY-SCHEDULER-BOUNDARY",
                                    line,
                                    "@Scheduled methods must stay in an adaptor business scheduler;"
                                            + " application services expose callable business actions");
                        }
                        // 构造器的纯装配/值复制不凑业务步骤，复杂构造器仍在设计复核清单。
                        super.visitMethod(method, ignored);
                        rows.add(
                                root.relativize(path).toString().replace('\\', '/')
                                        + ","
                                        + current
                                        + ","
                                        + line
                                        + ","
                                        + statements
                                        + ","
                                        + methodFlows
                                        + ","
                                        + (methodFlows > 0
                                                ? "flow_checked"
                                                : "single_operation_or_constructor_review"));
                        current = previous;
                        methodFlows = previousFlows;
                        return null;
                    }

                    @Override
                    public Void visitLambdaExpression(LambdaExpressionTree lambda, Void ignored) {
                        if (lambda.getBody() instanceof BlockTree block) check(block, "lambda");
                        return super.visitLambdaExpression(lambda, ignored);
                    }

                    @Override
                    public Void visitTry(TryTree attempt, Void ignored) {
                        check(attempt.getBlock(), "try");
                        return super.visitTry(attempt, ignored);
                    }

                    @Override
                    public Void visitNewClass(NewClassTree creation, Void ignored) {
                        if (path.toString().replace('\\', '/').contains("/application/")
                                && !path.toString().replace('\\', '/').contains("/assembler/")
                                && creation.getIdentifier()
                                        .toString()
                                        .matches(".*[A-Z][A-Za-z0-9]*Entity")) {
                            fail(
                                    "QUALITY-DOMAIN",
                                    unit.getLineMap()
                                            .getLineNumber(
                                                    positions.getStartPosition(unit, creation)),
                                    "application flow must use aggregate creation/state methods,"
                                            + " not construct Entity fields");
                        }
                        // 映射按源码包职责检查，内部标准Result及领域自己的DO不受限制。
                        String packageName = unit.getPackageName().toString();
                        String type = creation.getIdentifier().toString().replaceAll("<.*>", "");
                        boolean assembler =
                                packageName.matches(".*\\.assembler(\\..*)?")
                                        || path.getFileName()
                                                .toString()
                                                .endsWith("Assembler.java");
                        boolean converter =
                                packageName.matches(".*\\.converter(\\..*)?")
                                        || path.getFileName()
                                                .toString()
                                                .endsWith("Converter.java");
                        boolean misplacedMapping =
                                (!assembler && packageName.contains(".application.")
                                        && type.matches(".+(Command|Param|Result)"))
                                || (!assembler
                                        && packageName.matches(
                                                ".*\\.adaptor\\..+\\.input(\\..*)?")
                                        && type.matches(".+(Command|Response)"))
                                || (!converter && packageName.contains(".adaptor.")
                                        && packageName.contains(".output")
                                        && type.matches(".+DO"));
                        if (misplacedMapping) {
                            fail("QUALITY-MAPPING",
                                    unit.getLineMap().getLineNumber(
                                            positions.getStartPosition(unit, creation)),
                                    "construct mapped DTO in its layer assembler/converter; "
                                            + "pass complete source objects to purpose-specific methods");
                        }
                        // 层内创建本层异常；下层已分类异常由公共BaseException保留。
                        for (String owner : List.of("Domain", "Application", "Infrastructure", "Adaptor")) {
                            if (type.endsWith(owner + "Exception")
                                    && !packageName.contains("." + owner.toLowerCase(Locale.ROOT) + ".")) {
                                fail("QUALITY-ERROR-OWNER",
                                        unit.getLineMap().getLineNumber(
                                                positions.getStartPosition(unit, creation)),
                                        "create the current module exception instead of " + type);
                            }
                        }
                        return super.visitNewClass(creation, ignored);
                    }

                    @Override
                    public Void visitMethodInvocation(
                            MethodInvocationTree invocation, Void ignored) {
                        if (path.toString().replace('\\', '/').contains("/domain/")
                                && path.toString().replace('\\', '/').contains("/service/")
                                && invocation.getMethodSelect() instanceof MemberSelectTree select
                                && select.getIdentifier().contentEquals("entity")) {
                            fail(
                                    "QUALITY-DOMAIN",
                                    unit.getLineMap()
                                            .getLineNumber(
                                                    positions.getStartPosition(unit, invocation)),
                                    "domain service must collaborate through aggregate semantic"
                                            + " methods");
                        }
                        return super.visitMethodInvocation(invocation, ignored);
                    }

                    void check(BlockTree original, String kind) {
                        BlockTree body = original;
                        while (body.getStatements().size() == 1
                                && body.getStatements().get(0) instanceof TryTree attempt) {
                            body = attempt.getBlock();
                        }
                        if (!checked.add(body) || body.getStatements().size() < 2) return;
                        flows++;
                        methodFlows++;
                        int expected = 1, found = 0;
                        long start = positions.getStartPosition(unit, body) + 1;
                        for (StatementTree statement : body.getStatements()) {
                            long end = positions.getStartPosition(unit, statement);
                            if (start < 0 || end < start) continue;
                            Matcher matcher =
                                    STEP.matcher(source.substring((int) start, (int) end));
                            while (matcher.find()) {
                                int number = Integer.parseInt(matcher.group(1));
                                if (number != expected)
                                    fail(
                                            "QUALITY-STEPS",
                                            unit.getLineMap().getLineNumber(end),
                                            kind
                                                    + " step must start at 1 and continue in order;"
                                                    + " expected "
                                                    + expected
                                                    + " but got "
                                                    + number);
                                expected = number + 1;
                                found++;
                                if (!Pattern.compile("\\p{IsHan}").matcher(matcher.group(2)).find())
                                    fail(
                                            "QUALITY-STEPS",
                                            unit.getLineMap().getLineNumber(end),
                                            "step description must explain responsibility in"
                                                    + " Chinese");
                                if (matcher.group(2)
                                        .matches("(?i)(第一步|第二步|第三步|获取|调用|返回|step|todo)[。.]?"))
                                    fail(
                                            "QUALITY-STEPS",
                                            unit.getLineMap().getLineNumber(end),
                                            "step description must name its responsibility");
                            }
                            start = positions.getEndPosition(unit, statement);
                        }
                        if (found < 2)
                            fail(
                                    "QUALITY-STEPS",
                                    unit.getLineMap()
                                            .getLineNumber(positions.getStartPosition(unit, body)),
                                    kind
                                            + " with multiple statements needs at least two"
                                            + " meaningful numbered stages");
                    }

                    void fail(String code, long line, String message) {
                        failures++;
                        System.err.println(
                                root.relativize(path)
                                        + ":"
                                        + line
                                        + ": "
                                        + code
                                        + " "
                                        + current
                                        + ": "
                                        + message);
                    }
                }.scan(unit, null);
            }
        }
        // 3. 解析失败同样阻断门禁，不把漏扫文件算作通过。
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                failures++;
                System.err.println(
                        "QUALITY-PARSE: "
                                + diagnostic.getSource().getName()
                                + ":"
                                + diagnostic.getLineNumber());
            }
        }
        // 4. 按需输出逐方法清单；默认Maven执行只打印汇总与违规位置。
        if (arguments.length == 3 && arguments[1].equals("--report")) {
            Path report = Path.of(arguments[2]);
            Files.createDirectories(report.toAbsolutePath().getParent());
            Files.write(report, rows, StandardCharsets.UTF_8);
        }
        // 5. 有违规时以非零退出，validate在javac之前停止。
        System.out.println(
                "Java business quality: classes="
                        + files.size()
                        + ", methods="
                        + methods
                        + ", flow_blocks="
                        + flows
                        + ", violations="
                        + failures);
        if (failures > 0) System.exit(1);
    }

    private static int count(Tree tree) {
        return new TreeScanner<Integer, Void>() {
            @Override
            public Integer scan(Tree node, Void ignored) {
                if (node == null) return 0;
                int own =
                        node instanceof StatementTree
                                        && !(node instanceof BlockTree)
                                        && !(node instanceof ClassTree)
                                ? 1
                                : 0;
                Integer children = super.scan(node, ignored);
                return own + (children == null ? 0 : children);
            }

            @Override
            public Integer reduce(Integer first, Integer second) {
                return (first == null ? 0 : first) + (second == null ? 0 : second);
            }
        }.scan(tree, null);
    }
}
