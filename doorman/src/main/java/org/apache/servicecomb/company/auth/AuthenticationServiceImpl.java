/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicecomb.company.auth;

import org.apache.servicecomb.company.auth.domain.User;
import org.apache.servicecomb.company.auth.domain.UserRepository;

class AuthenticationServiceImpl implements AuthenticationService {

  private final TokenStore tokenStore;
  private final UserRepository userRepository;

  AuthenticationServiceImpl(
      TokenStore tokenStore,
      UserRepository userRepository) {
    this.tokenStore = tokenStore;
    this.userRepository = userRepository;
  }

  @Override
  public String authenticate(String username, String password) {
    User user = userRepository.findByUsernameAndPassword(username, password);

    if (user == null) {
      throw new UnauthorizedAccessException("No user matches username " + username + " and password");
    }

    return tokenStore.generate(username);
  }

  @Override
  public String validate(String token) {
    try {
      return tokenStore.parse(token);
    } catch (TokenException e) {
      throw new UnauthorizedAccessException("No user matches such a token " + token, e);
    }
  }
}