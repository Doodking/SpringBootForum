<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>
<@c.page>
<div>
    <@l.logout />
</div>
<div>Список постов</div>
<form method="post" action="/filter">
    <input type="text" name="filter">
    <button type="submit">Найти!</button>
</form>
<#list posts as post>
<div>
    <b>${post.id}</b>
    <span>${post.text}</span>
    <i>${post.tag}</i>
    <strong>${post.authorName}</strong>
</div>
<#else>
No posts
</#list>
</@c.page>