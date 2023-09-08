# WebSecurity

![Main](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.09.43%20AM.png)

## Learning best practices

The purpose of this project was to introduce us to some basic web security concepts such as cryptography, certificates, HTTP, and other secure protocols. Our goal was to create an app that manages certificates for other users, like B2C SaaS where people can come generate digital certificates that they need for signing or other confirmation purposes and we will do everything for them in one place.

![All](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.29.35%20AM.png)

## Our solution

Our solution enabled users to log in with their username and password or with OAuth2.0 protocol via GitHub. If they choose to use a regular old username and password we make sure that the password is hashed in our MongoDB NoSQL database, that the user changed it no less than 1 month ago, and that it has proper strength for highly critical apps like this one. After logging in they were presented with all certificates issued by our organization and CA, here They were able to request their own CA or Root certificate and download it once the request was approved. Then if needed they were able to check if some other certificate was valid or not and to request for the certificate to be revoked. All communication 
was done through a secure https protocol so no data or sensitive information were leaked. For this project before implementing the solution, we needed to first research the best way to create that functionality...

![Verify1](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.29.43%20AM.png)
![Verify2](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.29.54%20AM.png)
![Verify3](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.30.11%20AM.png)

## Summary

The security part of software development is a big and important field and we are looking forward to coming back to it one day.
