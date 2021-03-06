package me.choi.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Test
    @WithAnonymousUser
    public void index_anonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUser
    public void index_user() throws Exception {
        // 이미 이러한 유저가 이미 로그인을 한 상태라고 가정
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUser
    public void admin_user() throws Exception {
        // 이미 이러한 유저가 이미 로그인을 한 상태라고 가정
        mockMvc.perform(get("/admin").with(user("junwoo").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAdmin
    public void admin_admin() throws Exception {
        // 이미 이러한 유저가 이미 로그인을 한 상태라고 가정
        mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void login_success() throws Exception {
        String username = "junwoo";
        String password = "123";
        Account account = this.createUser(username, password);

        accountService.createNew(account);
        mockMvc.perform(formLogin().user(account.getUsername()).password(password))
                .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void login_fail() throws Exception {
        String username = "junwoo";
        String password = "123";
        Account account = this.createUser(username, password);

        accountService.createNew(account);
        mockMvc.perform(formLogin().user(account.getUsername()).password("!@#"))
                .andExpect(unauthenticated());
    }

    private Account createUser(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("USER");
        return account;
    }

}
