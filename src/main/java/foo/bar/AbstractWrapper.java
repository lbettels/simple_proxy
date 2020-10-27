/**
 * lbettels
 *
 * Copyright (C) 2020 lbettels
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foo.bar;

import java.sql.SQLException;
import java.sql.Wrapper;

public abstract class AbstractWrapper implements Wrapper {

    private final Object delegate;

    protected AbstractWrapper(Object delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        final Object result;
        if (iface.isAssignableFrom(getClass())) {
            // if the proxy directly implements the interface or extends it, return the proxy
            result = this;
        } else if (iface.isAssignableFrom(delegate.getClass())) {
            // if the proxied object directly implements the interface or extends it, return
            // the proxied object
            result = unwrapProxy();
        } else if (Wrapper.class.isAssignableFrom(delegate.getClass())) {
            // if the proxied object implements the wrapper interface, then
            // return the result of it's unwrap method.
            result = ((Wrapper) unwrapProxy()).unwrap(iface);
        } else {
      /*
          This line of code can only be reached when the underlying object does not implement the wrapper
          interface.  This would mean that either the JDBC driver or the wrapper of the underlying object
          does not implement the JDBC 4.0 API.
        */
            throw new SQLException("Can not unwrap to " + iface.getName());
        }
        return iface.cast(result);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(getClass())) {
            // if the proxy directly proxy the interface or extends it, return true
            return true;
        } else if (iface.isAssignableFrom(delegate.getClass())) {
            // if the proxied object directly implements the interface or extends it, return true
            return true;
        } else if (Wrapper.class.isAssignableFrom(delegate.getClass())) {
            // if the proxied object implements the wrapper interface, then
            // return the result of it's isWrapperFor method.
            return ((Wrapper) unwrapProxy()).isWrapperFor(iface);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public Object unwrapProxy() {
        return delegate;
    }
}
