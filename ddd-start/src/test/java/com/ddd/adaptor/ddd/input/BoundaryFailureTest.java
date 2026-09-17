package com.ddd.adaptor.ddd.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ddd.adaptor.ddd.output.DddOutputAdaptorImpl;
import com.ddd.application.ddd.assembler.DddApplicationAssembler;
import com.ddd.application.ddd.command.DddWriteCommand;
import com.ddd.application.ddd.service.DddCalculateApplication;
import com.ddd.application.ddd.service.DddExternalReadApplication;
import com.ddd.application.ddd.service.DddReadApplication;
import com.ddd.application.ddd.service.DddRuleApplication;
import com.ddd.application.ddd.service.DddWriteApplication;
import com.ddd.common.result.Result;
import com.ddd.domain.ddd.service.DddCalculateDomainService;
import com.ddd.domain.ddd.service.DddRuleDomainService;
import com.ddd.domain.ddd.service.DddWriteDomainService;
import com.ddd.model.ddd.DddWriteDO;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * 主入口异常与事务提交失败的有限回归，不等同独立代码评审。
 *
 * @author AIGenerator
 */
class BoundaryFailureTest {
    @Test
    void everyMainEntryReturnsFailureForCollaboratorExceptions() throws Exception {
        int checked = 0;
        for (Class<?> type :
                List.of(
                        DddController.class,
                        DddWriteApplication.class,
                        DddReadApplication.class,
                        DddCalculateApplication.class,
                        DddRuleApplication.class,
                        DddExternalReadApplication.class,
                        DddWriteDomainService.class,
                        DddCalculateDomainService.class,
                        DddRuleDomainService.class,
                        DddOutputAdaptorImpl.class)) {
            var constructor = type.getConstructors()[0];
            Object[] dependencies =
                    Arrays.stream(constructor.getParameterTypes())
                            .map(
                                    t ->
                                            mock(
                                                    t,
                                                    invocation -> {
                                                        throw new IllegalStateException(
                                                                "private-collaborator-error");
                                                    }))
                            .toArray();
            Object service = constructor.newInstance(dependencies);
            for (var method : type.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())
                        && method.getReturnType() == Result.class) {
                    Result<?> result =
                            (Result<?>)
                                    method.invoke(service, new Object[method.getParameterCount()]);
                    assertFalse(result.success(), type.getSimpleName() + "." + method.getName());
                    assertFalse(result.message().contains("private-collaborator-error"));
                    checked++;
                }
            }
        }
        assertEquals(14, checked);
    }

    @Test
    void transactionCommitFailureIsCaughtBeforeLeavingApplication() {
        var manager = mock(PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        doThrow(new IllegalStateException("private-commit-error")).when(manager).commit(any());
        var domain = mock(DddWriteDomainService.class);
        when(domain.write(any())).thenReturn(Result.success(mock(DddWriteDO.class)));
        var application =
                new DddWriteApplication(domain, mock(DddApplicationAssembler.class), manager);
        var result = assertDoesNotThrow(() -> application.write(mock(DddWriteCommand.class)));
        assertFalse(result.success());
        assertEquals("APPLICATION_PROCESS_FAILED", result.code());
        verify(manager).commit(any());
    }
}
