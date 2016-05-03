package com.meiqi.data.render;

import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

/**
 * User: 
 * Date: 14-1-4
 * Time: 下午5:07
 */
public class RengineStringResourceRepository implements StringResourceRepository {
    /**
     * Current Repository encoding.
     */
    private String encoding = StringResourceLoader.REPOSITORY_ENCODING_DEFAULT;

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#getStringResource(java.lang.String)
     */
    public StringResource getStringResource(final String name) {
        return new StringResource(name, getEncoding());
    }

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#putStringResource(java.lang.String, java.lang.String)
     */
    public void putStringResource(final String name, final String body) {
        //
    }

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#putStringResource(java.lang.String, java.lang.String, java.lang.String)
     * @since 1.6
     */
    public void putStringResource(final String name, final String body, final String encoding) {
        //
    }

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#removeStringResource(java.lang.String)
     */
    public void removeStringResource(final String name) {
        //
    }

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#getEncoding()
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @see org.apache.velocity.runtime.resource.util.StringResourceRepository#setEncoding(java.lang.String)
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}