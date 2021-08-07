<html>
    <body>
        <div>Welcome ${displayName}</div>
        <ul>
           <#list phrases as phrase>
               <li>${phrase}</li>
           </#list>
        </ul>
    </body>
</html>