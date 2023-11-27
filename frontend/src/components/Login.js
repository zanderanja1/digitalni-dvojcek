import { useContext, useState } from 'react';
import { UserContext } from '../userContext';
import { Navigate } from 'react-router-dom';

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const userContext = useContext(UserContext);

    async function Login(e) {
        e.preventDefault();
        const res = await fetch("http://localhost:3001/users/login", {
            method: "POST",
            credentials: "include",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        const data = await res.json();
        if (data._id !== undefined) {
            userContext.setUserContext(data);
        } else {
            setUsername("");
            setPassword("");
            setError("Invalid username or password");
        }
    }

    return (
        <div className='registerForm'>
            <form onSubmit={Login} className="form">
                <h4 className='registracijaText'>Prijava </h4>
                {userContext.user ? <Navigate replace to="/" /> : ""}
                <input type="text" name="username" placeholder="Username"
                    value={username} onChange={(e) => (setUsername(e.target.value))} autoComplete='off'/>
                <input type="password" name="password" placeholder="Password"
                    value={password} onChange={(e) => (setPassword(e.target.value))} autoComplete='off' />
                <input type="submit" name="submit" value="Log in" />
                <label>{error}</label>
            </form>
        </div>
    );
}

export default Login;