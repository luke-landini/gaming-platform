<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="alert alert-error">
            <#if message?has_content>
                <p>${message.summary}</p>
            <#else>
                <p>An error occurred. Please try again.</p>
            </#if>
        </div>
        <div class="form-group">
            <a href="${url.loginRestartFlowUrl}" class="btn">Back to Login</a>
        </div>
    </#if>
</@layout.registrationLayout>
