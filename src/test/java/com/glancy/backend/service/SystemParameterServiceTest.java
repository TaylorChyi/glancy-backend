package com.glancy.backend.service;

import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.repository.SystemParameterRepository;
import com.glancy.backend.entity.SystemParameter;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SystemParameterServiceTest {

    @Autowired
    private SystemParameterService parameterService;
    @Autowired
    private SystemParameterRepository parameterRepository;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    @BeforeEach
    void setUp() {
        parameterRepository.deleteAll();
    }

    @Test
    void testUpsertCreateAndUpdate() {
        // 创建一个新参数
        SystemParameterRequest req = new SystemParameterRequest();
        req.setName("site.title");
        req.setValue("Glancy");
        SystemParameterResponse resp = parameterService.upsert(req);
        assertNotNull(resp.getId());
        assertEquals("Glancy", resp.getValue());

        // 更新同名参数的值
        SystemParameterRequest updateReq = new SystemParameterRequest();
        updateReq.setName("site.title");
        updateReq.setValue("NewTitle");
        SystemParameterResponse updated = parameterService.upsert(updateReq);
        assertEquals(resp.getId(), updated.getId());
        assertEquals("NewTitle", updated.getValue());

        SystemParameter entity = parameterRepository.findById(resp.getId()).orElseThrow();
        assertEquals("NewTitle", entity.getValue());
    }

    @Test
    void testGetByName() {
        // 先写入一条数据
        SystemParameter param = new SystemParameter();
        param.setName("email.notifications.enabled");
        param.setValue("true");
        parameterRepository.save(param);

        // 根据名称读取
        SystemParameterResponse resp = parameterService.getByName("email.notifications.enabled");
        assertEquals("true", resp.getValue());
    }

    @Test
    void testListParameters() {
        // 写入多条数据
        SystemParameter p1 = new SystemParameter();
        p1.setName("a");
        p1.setValue("1");
        SystemParameter p2 = new SystemParameter();
        p2.setName("b");
        p2.setValue("2");
        parameterRepository.save(p1);
        parameterRepository.save(p2);

        // 列出所有参数
        assertEquals(2, parameterService.list().size());
    }
}
