package com.example.bolta_justin.Token.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenService {
    boolean checkBlackList(String token, HttpServletResponse response) throws IOException;
}
