package com.zjgsu.scy.monolith.controller;

import com.zjgsu.scy.monolith.model.Course;
import com.zjgsu.scy.monolith.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityApiTest {
    @Autowired MockMvc mvc;
    @Autowired ActivityRepository activities;
    @Autowired CourseRepository courses;
    @Test void completeFlowAndConflictResponses() throws Exception {
        String json = "{\"name\":\"桌游之夜\",\"location\":\"活动室\",\"startsAt\":\"" + LocalDateTime.now().plusDays(1) + "\",\"capacity\":1}";
        mvc.perform(post("/api/activities").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value("桌游之夜"));
        Long id = activities.findAll().getFirst().getId();
        mvc.perform(get("/api/activities")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/api/activities/" + id)).andExpect(status().isOk());
        String path = "/api/activities/" + id + "/registrations";
        String member = "{\"memberNo\":\"2412190325\",\"memberName\":\"施晨煜\"}";
        mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(member))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.activityId").value(id));
        mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(member))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value("该成员已报名，请勿重复报名"));
        mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
            .content("{\"memberNo\":\"other\",\"memberName\":\"成员乙\"}"))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value("活动人数已满"));
        mvc.perform(get(path)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void missingActivityReturns404() throws Exception {
        mvc.perform(get("/api/activities/999999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/activities/999999/registrations")).andExpect(status().isNotFound());
        mvc.perform(post("/api/activities/999999/registrations").contentType(MediaType.APPLICATION_JSON)
            .content("{\"memberNo\":\"one\",\"memberName\":\"成员甲\"}"))
            .andExpect(status().isNotFound());
    }
    @Test void invalidActivityReturns400() throws Exception {
        mvc.perform(post("/api/activities").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\",\"location\":\"活动室\",\"startsAt\":\"2000-01-01T10:00:00\",\"capacity\":0}"))
            .andExpect(status().isBadRequest());
    }
    @Test void invalidMemberReturns400() throws Exception {
        mvc.perform(post("/api/activities/1/registrations").contentType(MediaType.APPLICATION_JSON)
            .content("{\"memberNo\":\" \",\"memberName\":\"\"}"))
            .andExpect(status().isBadRequest());
    }
    @Test void coursesEndpointReadsPreparedCourse() throws Exception {
        courses.saveAndFlush(new Course("MS04", "微服务架构与实践"));
        mvc.perform(get("/api/courses")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("MS04"));
    }
}
