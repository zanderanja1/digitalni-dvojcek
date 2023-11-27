import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../userContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser } from '@fortawesome/free-solid-svg-icons';
function Profile() {

    const { user } = useContext(UserContext);

    const [profile, setProfile] = useState({});

    useEffect(() => {
        const getProfile = async () => {
            try {
                /*
                const resUser = await fetch("http://localhost:3001/users/profile", { credentials: "include" });
                const userData = await resUser.json();
                setProfile(userData);
                */

                const resCities = await fetch("http://localhost:3001/cities");
                const citiesData = await resCities.json();
                setProfile(prevProfile => ({
                    ...prevProfile,
                    favouriteCities: citiesData.filter(city => city.favouritesBy.includes(user._id))
                }));
            } catch (error) {
                console.log("Error fetching data:", error);
            }
        }
        getProfile();
    }, [user._id]);

    return (
        <>

            <div className="centerContainer">
                <div className="profileContainer">
                    <div className='naslovProfile'>
                        <h3>Uporabniški profil<FontAwesomeIcon icon={faUser} style={{ marginLeft: '15px' }} /></h3>
                    </div>
                    <div className='profileInfo'>
                        <p className='infoSpec'>Uporabniško ime: {user.username}</p>
                        <p className='infoSpec'>Email: {user.email}</p>
                        <p className='infoSpec'>Ime: {user.name}</p>
                        <p className='infoSpec'>Priimek: {user.surname}</p>

                        <h4>Priljubljena mesta:</h4>
                        {profile.favouriteCities && profile.favouriteCities.length > 0 ? (
                            <>
                                <ul>
                                    {profile.favouriteCities.map(city => (
                                        <li key={city._id}>{city.name}</li>
                                    ))}
                                </ul>
                            </>
                        ) :

                            <div className='missingFavourites'>
                                <p>Nimate pribljubljenih mest</p>
                            </div>
                        }

                    </div>
                </div>
            </div>

        </>
    );
}

export default Profile;
