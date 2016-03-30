/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.ozark.util;

import java.util.Objects;

/**
 * Utility methods for path and URI handling.
 *
 * @author Santiago Pericas-Geertsen
 */
public final class PathUtils {

    /**
     * Drops starting slash from path if present.
     *
     * @param path the path.
     * @return the resulting path without a starting slash.
     */
    public static String noStartingSlash(String path) {
        Objects.requireNonNull(path, "path must not be null");
        return hasStartingSlash(path) ? path.substring(1) : path;
    }

    /**
     * Determines of path starts with a slash.
     *
     * @param path the path to test.
     * @return outcome of test.
     */
    public static boolean hasStartingSlash(String path) {
        Objects.requireNonNull(path, "path must not be null");
        return !"".equals(path) && path.charAt(0) == '/';
    }

    /**
     * Drops a prefix from a path if it exists or returns original path if prefix does
     * not match.
     *
     * @param path the path.
     * @param prefix the prefix to drop.
     * @return new path without prefix or old path if prefix does not exist.
     */
    public static String noPrefix(String path, String prefix) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(prefix, "prefix must not be null");
        return path.startsWith(prefix) ? path.substring(prefix.length()) : path;
    }

    /**
     * Ensures that a path always starts with a slash.
     *
     * @param path the path.
     * @return path that starts with a slash.
     */
    public static String ensureStartingSlash(String path) {
        Objects.requireNonNull(path, "path must not be null");
        return "".equals(path) || path.charAt(0) != '/' ? ("/" + path) : path;
    }

    /**
     * Ensures that a path always ends with a slash.
     *
     * @param path the path.
     * @return path that starts with a slash.
     */
    public static String ensureEndingSlash(String path) {
        Objects.requireNonNull(path, "path must not be null");
        return "".equals(path) || path.charAt(path.length() - 1) != '/' ? (path + "/") : path;
    }

    /**
     * Ensures that a path does not end with a slash. May return an
     * empty string.
     *
     * @param path the path.
     * @return path not ending with slash or empty string.
     */
    public static String ensureNotEndingSlash(String path) {
        Objects.requireNonNull(path, "path must not be null");
        if ("".equals(path)) {
            return path;
        }
        final int length = path.length();
        return path.charAt(length - 1) == '/' ? path.substring(0, length - 1) : path;
    }

    /**
     * Returns a normalized path. If the path is empty or "/*", then an empty string
     * is returned. Otherwise, the path returned always starts with a "/" but does not
     * end with one.
     *
     * @param path the path to normalize.
     * @return normalized path.
     */
    public static String normalizePath(String path) {
        Objects.requireNonNull(path, "path must not be null");
        return (path.isEmpty() || path.equals("/*")) ? "" : ensureNotEndingSlash(ensureStartingSlash(path));
    }
    
}
