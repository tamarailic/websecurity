# WebSecurity

![Main](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.09.43%20AM.png)

## Learning best practices

Purpose of this project was to introduce us in some basic websecurity concepts as criptography, certificates, https and other secure protocols. Our goal was to create app that manages certificates for other users, like B2C SaaS where people can come generate digital certificates that they need for signing or other confirmation purposes and we will do everything for them in one place.

![All](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.29.35%20AM.png)

## Our solution

Our solution enabled users to login with their username and password or with OAuth2.0 protocol via GitHub. If they choose to use regular old username and password we made sure that password is hashed in our MongoDB NoSQL database, that user changed it no less then 1 month ago and that it has proper strangth for highly critical app like this one. After logging in they were presented with all certificates issued by our organization and CA, here they were able to request their own CA or Root certificate and to download it once request was approved. Then if needed they were able to check if some other certificate is valid or not and to request for certificate to be revoked. All comunication all
was done through secure https protocol so no data or sensitive informations were leeked. For this project before implementing the solution we needed to first resurch best way to create that functionality...

![Verify1](https://github.com/UPocek/WebSecurity/blob/main/docs/Screenshot%202023-09-09%20at%2012.29.43%20AM.png)
