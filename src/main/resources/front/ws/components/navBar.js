import Link from 'next/link';
import navStyle from '../styles/Navbar.module.css'
import Image from 'next/image';

export default function NavBar() {

    const menuItems = ['Home', 'Verify']

    return (
        <nav className={navStyle.navbarStyle}>
            <Image src="/../public/images/ws_logo.png" width={330} height={90} alt='ws logo' />
            <ul className={navStyle.navUl}>
                {menuItems.map(item => <li key={item}><Link href={`/${item != 'Home' ? item.toLowerCase() : ''}`} className={navStyle.navItem}>{item}</Link></li>)}
            </ul>
            <ul className={navStyle.navUl}>
                <li><Link href="/profile" className={navStyle.navItem}>Profile</Link></li>
                <Link className={navStyle.signOut} href="/">Sign out</Link>
            </ul>
        </nav>
    );
}