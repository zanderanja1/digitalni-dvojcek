import { NavLink } from 'react-router-dom'
import { UserContext } from '../userContext'
import { useState } from 'react'
import { useContext } from 'react'
const Navbar = () => {

    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const handleDropdownToggle = () => {
        setIsDropdownOpen(!isDropdownOpen);
    };


    const { user } = useContext(UserContext);

    return (
        <nav className="navbar">
          <div className="container">
            <div className="nav-elements">
              <ul>
                <li className="home">
                  <NavLink to="/">Domov</NavLink>
                </li>
                <li>
                  <NavLink to="/cities">Urna napoved</NavLink>
                </li>
                <li>
                  <NavLink to="/citiesTen">8-dnevna napoved</NavLink>
                </li>
                {user ? (
                  <li>
                    <NavLink to="/favs">Favourites</NavLink>
                  </li>
                ) : (
                  <div className="logREgDiv">
                    <NavLink to="/login">Prijava</NavLink>
                    <span className="spacer"></span>
                    <NavLink to="/register">Registracija</NavLink>
                  </div>
                )}
              </ul>
            </div>
          </div>
    
          {user && (
            <div className="dropdown">
              <p className="loggedAs" onClick={handleDropdownToggle}>
                Prijavljeni ste kot: {user.username}
              </p>
    
              {isDropdownOpen && (
                <div className="dropdown-menu">
                  <NavLink to='/profile'>Profil</NavLink>
                  <NavLink to="/logout">Odjava</NavLink>
                </div>
              )}
            </div>
          )}
        </nav>
      );
}

export default Navbar;