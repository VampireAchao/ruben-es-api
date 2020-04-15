package com.ruben.rubenesapi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @ClassName: User
 * @Date: 2020/4/15 18:09
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class User {
    private String name;
    private int age;
}
