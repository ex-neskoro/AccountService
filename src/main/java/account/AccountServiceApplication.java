package account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

}