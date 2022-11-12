package com.spsgame;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
class SpsGameApplicationTests {

    @MockBean
    private GameController controller;

    @Test
    public void toDoRestClientTest()  {/*
        given(this.controller.init())
                .willReturn(Player.builder().playerId("123").build());
        assertThat(
                this.controller.init().getPlayerId())
                .isEqualTo("123");*/

    }
}
