/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.ozark.jersey;

import org.glassfish.jersey.message.internal.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility for selecting variant that best matches request from a list of variants.
 * This class is based on {@link org.glassfish.jersey.message.internal.VariantSelector}.
 *
 * @author Paul Sandoz
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Santiago Pericas-Geertsen
 */
public final class VariantSelector {

    static final AcceptableMediaType GENERAL_ACCEPT_MEDIA_TYPE =
            new AcceptableMediaType("*", "*");

    static final List<AcceptableMediaType> GENERAL_ACCEPT_MEDIA_TYPE_LIST =
            Collections.singletonList(GENERAL_ACCEPT_MEDIA_TYPE);

    private VariantSelector() {
    }

    /**
     * Interface to get a dimension value from a variant and check if an
     * acceptable dimension value is compatible with a dimension value.
     */
    private interface DimensionChecker<T, U> {

        /**
         * Get the dimension value from the variant.
         *
         * @param v the variant.
         * @return the dimension value.
         */
        U getDimension(VariantHolder v);

        /**
         * Get the quality source of the dimension.
         *
         * @return quality source.
         */
        int getQualitySource(VariantHolder v, U u);

        /**
         * Ascertain if the acceptable dimension value is compatible with
         * the dimension value.
         *
         * @param t the acceptable dimension value.
         * @param u the dimension value.
         * @return {@code true} if the acceptable dimension value is compatible with
         * the dimension value.
         */
        boolean isCompatible(T t, U u);

        /**
         * Get the value of the Vary header.
         *
         * @return the value of the Vary header.
         */
        String getVaryHeaderValue();
    }

    private static final DimensionChecker<AcceptableMediaType, MediaType> MEDIA_TYPE_DC =
            new DimensionChecker<AcceptableMediaType, MediaType>() {

                @Override
                public MediaType getDimension(VariantHolder v) {
                    return v.v.getMediaType();
                }

                @Override
                public boolean isCompatible(AcceptableMediaType t, MediaType u) {
                    return t.isCompatible(u);
                }

                @Override
                public int getQualitySource(VariantHolder v, MediaType u) {
                    return v.mediaTypeQs;
                }

                @Override
                public String getVaryHeaderValue() {
                    return HttpHeaders.ACCEPT;
                }
            };
    private static final DimensionChecker<AcceptableLanguageTag, Locale> LANGUAGE_TAG_DC =
            new DimensionChecker<AcceptableLanguageTag, Locale>() {

                @Override
                public Locale getDimension(VariantHolder v) {
                    return v.v.getLanguage();
                }

                @Override
                public boolean isCompatible(AcceptableLanguageTag t, Locale u) {
                    return t.isCompatible(u);
                }

                @Override
                public int getQualitySource(VariantHolder qsv, Locale u) {
                    return Quality.MINIMUM;
                }

                @Override
                public String getVaryHeaderValue() {
                    return HttpHeaders.ACCEPT_LANGUAGE;
                }
            };
    private static final DimensionChecker<AcceptableToken, String> CHARSET_DC =
            new DimensionChecker<AcceptableToken, String>() {

                @Override
                public String getDimension(VariantHolder v) {
                    MediaType m = v.v.getMediaType();
                    return (m != null) ? m.getParameters().get("charset") : null;
                }

                @Override
                public boolean isCompatible(AcceptableToken t, String u) {
                    return t.isCompatible(u);
                }

                @Override
                public int getQualitySource(VariantHolder qsv, String u) {
                    return Quality.MINIMUM;
                }

                @Override
                public String getVaryHeaderValue() {
                    return HttpHeaders.ACCEPT_CHARSET;
                }
            };
    private static final DimensionChecker<AcceptableToken, String> ENCODING_DC =
            new DimensionChecker<AcceptableToken, String>() {

                @Override
                public String getDimension(VariantHolder v) {
                    return v.v.getEncoding();
                }

                @Override
                public boolean isCompatible(AcceptableToken t, String u) {
                    return t.isCompatible(u);
                }

                @Override
                public int getQualitySource(VariantHolder qsv, String u) {
                    return Quality.MINIMUM;
                }

                @Override
                public String getVaryHeaderValue() {
                    return HttpHeaders.ACCEPT_ENCODING;
                }
            };

    /**
     * Select variants for a given dimension.
     *
     * @param variantHolders   collection of variants.
     * @param acceptableValues the list of acceptable dimension values, ordered by the quality
     *                         parameter, with the highest quality dimension value occurring
     *                         first.
     * @param dimensionChecker the dimension checker
     * @param vary             output list of generated vary headers.
     */
    private static <T extends Qualified, U> LinkedList<VariantHolder> selectVariants(
            List<VariantHolder> variantHolders,
            List<T> acceptableValues,
            DimensionChecker<T, U> dimensionChecker,
            Set<String> vary) {
        int cq = Quality.MINIMUM;
        int cqs = Quality.MINIMUM;

        final LinkedList<VariantHolder> selected = new LinkedList<VariantHolder>();

        // Iterate over the acceptable entries
        // This assumes the entries are ordered by the quality
        for (final T a : acceptableValues) {
            final int q = a.getQuality();

            final Iterator<VariantHolder> iv = variantHolders.iterator();
            while (iv.hasNext()) {
                final VariantHolder v = iv.next();

                // Get the dimension  value of the variant to check
                final U d = dimensionChecker.getDimension(v);

                if (d != null) {
                    vary.add(dimensionChecker.getVaryHeaderValue());
                    // Check if the acceptable entry is compatable with
                    // the dimension value
                    final int qs = dimensionChecker.getQualitySource(v, d);
                    if (qs >= cqs && dimensionChecker.isCompatible(a, d)) {
                        if (qs > cqs) {
                            cqs = qs;
                            cq = q;
                            // Remove all entries that were added for qs < cqs
                            selected.clear();
                            selected.add(v);
                        } else if (q > cq) {
                            cq = q;
                            // Add variant with higher accept quality at the front
                            selected.addFirst(v);
                        } else if (q == cq) {
                            // Ensure selection is stable with order of variants
                            // with same quality of source and accept quality
                            selected.add(v);
                        }
                        iv.remove();
                    }
                }
            }
        }

        // Add all variants that are not compatible with this dimension
        // to the end
        for (VariantHolder v : variantHolders) {
            if (dimensionChecker.getDimension(v) == null) {
                selected.add(v);
            }
        }
        return selected;
    }

    private static class VariantHolder {

        private final Variant v;
        private final int mediaTypeQs;

        public VariantHolder(Variant v) {
            this(v, Quality.DEFAULT);
        }

        public VariantHolder(Variant v, int mediaTypeQs) {
            this.v = v;
            this.mediaTypeQs = mediaTypeQs;
        }
    }

    private static LinkedList<VariantHolder> getVariantHolderList(final List<Variant> variants) {
        final LinkedList<VariantHolder> l = new LinkedList<VariantHolder>();
        for (Variant v : variants) {
            final MediaType mt = v.getMediaType();
            if (mt != null) {
                if (mt instanceof QualitySourceMediaType || mt.getParameters().
                        containsKey(Quality.QUALITY_SOURCE_PARAMETER_NAME)) {
                    int qs = QualitySourceMediaType.getQualitySource(mt);
                    l.add(new VariantHolder(v, qs));
                } else {
                    l.add(new VariantHolder(v));
                }
            } else {
                l.add(new VariantHolder(v));
            }
        }

        return l;
    }


    /**
     * Get a list of media types that are acceptable for a request.
     *
     * @return a read-only list of requested response media types sorted according
     * to their q-value, with highest preference first.
     */
    public static List<AcceptableMediaType> getQualifiedAcceptableMediaTypes(HttpServletRequest request) {
        final String value = request.getHeader(HttpHeaders.ACCEPT);

        if (value == null || value.length() == 0) {
            return Collections.unmodifiableList(GENERAL_ACCEPT_MEDIA_TYPE_LIST);
        }

        try {
            return Collections.unmodifiableList(HttpHeaderReader.readAcceptMediaType(value));
        } catch (ParseException e) {
            throw new InternalServerErrorException("Unable to parse request 'Accept' header");
        }
    }

    /**
     * Get a list of languages that are acceptable for the message.
     *
     * @return a read-only list of acceptable languages sorted according
     * to their q-value, with highest preference first.
     */
    public static List<AcceptableLanguageTag> getQualifiedAcceptableLanguages(HttpServletRequest request) {
        final String value = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        if (value == null || value.length() == 0) {
            return Collections.singletonList(new AcceptableLanguageTag("*", null));
        }

        try {
            return Collections.unmodifiableList(HttpHeaderReader.readAcceptLanguage(value));
        } catch (ParseException e) {
            throw new InternalServerErrorException("Unable to parse request 'Accept-Language' header");
        }
    }

    /**
     * Get the list of language tag from the "Accept-Charset" of an HTTP request.
     *
     * @return The list of AcceptableToken. This list
     * is ordered with the highest quality acceptable charset occurring first.
     */
    public static List<AcceptableToken> getQualifiedAcceptCharset(HttpServletRequest request) {
        final String acceptCharset = request.getHeader(HttpHeaders.ACCEPT_CHARSET);
        try {
            if (acceptCharset == null || acceptCharset.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptCharset);
        } catch (ParseException e) {
            throw new InternalServerErrorException("Unable to parse request 'Accept-Charset' header");
        }
    }

    /**
     * Get the list of language tag from the "Accept-Encoding" of an HTTP request.
     *
     * @return The list of AcceptableToken. This list
     * is ordered with the highest quality acceptable charset occurring first.
     */
    public static List<AcceptableToken> getQualifiedAcceptEncoding(HttpServletRequest request) {
        final String acceptEncoding = request.getHeader(HttpHeaders.ACCEPT_ENCODING);
        try {
            if (acceptEncoding == null || acceptEncoding.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptEncoding);
        } catch (ParseException e) {
            throw new InternalServerErrorException("Unable to parse request 'Accept-Encoding' header");
        }
    }

    /**
     * Select a single media type based on the headers in the request and a list of
     * possible variants.
     *
     * @param request  servlet request.
     * @param variants list of possible variants.
     * @return selected media type or {@code null} if no matches.
     */
    private static MediaType selectVariant(HttpServletRequest request, List<Variant> variants) {
        LinkedList<VariantHolder> vhs = getVariantHolderList(variants);
        Set<String> vary = new HashSet<String>();
        vhs = selectVariants(vhs, getQualifiedAcceptableMediaTypes(request), MEDIA_TYPE_DC, vary);
        vhs = selectVariants(vhs, getQualifiedAcceptableLanguages(request), LANGUAGE_TAG_DC, vary);
        vhs = selectVariants(vhs, getQualifiedAcceptCharset(request), CHARSET_DC, vary);
        vhs = selectVariants(vhs, getQualifiedAcceptEncoding(request), ENCODING_DC, vary);
        return vhs.isEmpty() ? null : vhs.get(0).v.getMediaType();
    }

    /**
     * Select a single media type based on the headers in the request and the @Produces
     * annotation on a resource.
     *
     * @param request servlet request.
     * @param resourceInfo information about the resource matched.
     * @return selected media type or {@code null} if no matches.
     */
    public static MediaType selectVariant(HttpServletRequest request, ResourceInfo resourceInfo) {
        Produces produces = resourceInfo.getResourceMethod().getAnnotation(Produces.class);
        if (produces == null) {
            produces = resourceInfo.getResourceClass().getAnnotation(Produces.class);
        }
        if (produces != null) {
            final String[] mediaTypes = produces.value();
            final List<Variant> variants = Arrays.asList(mediaTypes).stream().map((String mt) -> {
                return Variant.mediaTypes(MediaType.valueOf(mt)).build().get(0);
            }).collect(Collectors.toList());
            return selectVariant(request, variants);
        }
        return null;
    }
}
