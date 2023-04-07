import NavBar from "./navBar";

export default function PageContainer({ children }) {
    return (
        <>
            <NavBar />
            {children}
        </>
    );
}