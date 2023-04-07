export default function NavBar() {

    const menuItems = ['Home', 'Verify']


    return (
        <nav>
            <ul>
                {menuItems.map(item => <li key={item}>{item}</li>)}
            </ul>
        </nav>
    );
}