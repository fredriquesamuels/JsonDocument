/**
 * Copyright (C) 2016 Fredrique. Samuels.
 * All rights reserved.  Email: fredriquesamuels@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of EITHER:
 *   (1) The GNU Lesser General Public License as published by the Free
 *       Software Foundation; either version 2.1 of the License, or (at
 *       your option) any later version. The text of the GNU Lesser
 *       General Public License is included with this library in the
 *       file LICENSE.TXT.
 *   (2) The BSD-style license that is included with this library in
 *       the file LICENSE-BSD.TXT.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files
 * LICENSE.TXT and LICENSE-BSD.TXT for more details.
 *
 * @Author: Fredrique Samuels fredriquesamuels@gmail.com
 */
package org.tect.platform.document.hibernate;

public abstract class DbCredentials {
    public abstract String getUrl();
    public abstract String getUser();
    public abstract String getPassword();
    public abstract String getDriver();

    public static Builder builder(String url, DbTarget target) {
        return builder(url, target.getDriverName());
    }

    public static Builder builder(String url, String driver) {
        return new BuilderImpl(url, driver);
    }

    public interface Builder {
        Builder withUserAndPassword(String user, String password);
        DbCredentials build();
    }

    private static class BuilderImpl implements Builder {
        private final String url;
        private final String target;
        private String password;
        private String user;

        private BuilderImpl(String url, String target) {
            this.url = url;
            this.target = target;
            this.user = "";
            this.password = "";
        }

        @Override
        public Builder withUserAndPassword(String user, String password) {
            this.user = user;
            this.password = password;
            return this;
        }

        @Override
        public DbCredentials build() {
            return new CredentialsImpl(this);
        }
    }

    private static class CredentialsImpl extends DbCredentials {
        private final String url;
        private final String target;
        private final String password;
        private final String user;


        public CredentialsImpl(BuilderImpl builder) {
            this.url = builder.url;
            this.target = builder.target;
            this.user = builder.user;
            this.password = builder.password;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getUser() {
            return user;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getDriver() {
            return target;
        }
    }
}
