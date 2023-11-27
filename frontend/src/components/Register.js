import { useState } from 'react';

function Register() {
    const [username, setUsername] = useState([]);
    const [password, setPassword] = useState([]);

    const [name, setName] = useState([]);
    const [surname, setSurname] = useState([]);

    const [email, setEmail] = useState([]);
    const [error, setError] = useState([]);

    async function Register(e) {
        e.preventDefault();
        const res = await fetch("http://localhost:3001/users", {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: email,
                username: username,
                password: password,
                name: name,
                surname: surname
            })
        });
        const data = await res.json();
        if (data._id !== undefined) {
            window.location.href = "/";
        }
        else {
            setUsername("");
            setPassword("");
            setEmail("");
            setName("");
            setSurname("");
            setError("Registration failed");
        }
    }

    return (
        <div className="registerForm">
            <form onSubmit={Register} className="form">
                <h4 className='registracijaText'>Registracija</h4>
                <input type="text" name="email" placeholder="Email" value={email} onChange={(e) => (setEmail(e.target.value))} autoComplete='off'/><br></br>
                <input type="text" name="username" placeholder="Username" value={username} onChange={(e) => (setUsername(e.target.value))} autoComplete='off' /><br></br>
                <input type='text' name='name' placeholder='Ime' value={name} onChange={(e)=> (setName(e.target.value))} autoComplete='off' /><br></br>
                <input type='text' name='surname' placeholder='Priimek' value={surname} onChange={(e)=> (setSurname(e.target.value))} autoComplete='off' /><br></br>
                <input type="password" name="password" placeholder="Password" value={password} onChange={(e) => (setPassword(e.target.value))} autoComplete='off' /><br></br>
                <input type="submit" name="submit" value="Register" />
                <label>{error}</label>
            </form>
        </div>
    );
}

export default Register;