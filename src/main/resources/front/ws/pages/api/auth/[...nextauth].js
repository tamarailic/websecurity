import { axiosInstance, backUrl } from "@/components/pageContainer";
import NextAuth from "next-auth"
import GithubProvider from "next-auth/providers/github"
import { setAuthTokens } from "axios-jwt";
import Router from "next/router";

export const authOptions = {
  // Configure one or more authentication providers
  providers: [
    GithubProvider({
      clientId: process.env.GITHUB_ID,
      clientSecret: process.env.GITHUB_SECRET,
      redirectUri: 'https://127.0.0.1:3000/api/auth/callback/github'
    }),
  ],
  callbacks: {
    async signIn(user, account, profile) {
      axiosInstance.post(`${backUrl}/api/auth/creteOrLogin`, { 'username': user.user.email, 'fullName': user.user.name })
        .then(resp => {
          if (resp.status.isError) {
            console.log(resp);
          } else {
            setAuthTokens({
              accessToken: resp.data.accessToken,
              refreshToken: resp.data.refreshToken
            });
          }
        })
        .catch(err => {
          console.log(err);
        });
      return true;
    },
    async signOut() {
      return true;
    },
  },
}

export default NextAuth(authOptions)