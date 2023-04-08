import Head from "next/head";
import NavBar from "./navBar";

export const backUrl = 'http://localhost:8080';

export default function PageContainer({ children }) {
    return (
        <>
            <Head>
                <link rel="preconnect" href="https://fonts.googleapis.com" />
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100;200;300;400;500;600;700;800;900&display=swap" rel="stylesheet" />
            </Head>
            <NavBar />
            {children}
        </>
    );
}