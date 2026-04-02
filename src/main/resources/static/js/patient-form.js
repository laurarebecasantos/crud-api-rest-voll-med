document.getElementById('patientForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const data = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        cpf: document.getElementById('cpf').value,
        address: {
            street: document.getElementById('street').value,
            neighborhood: document.getElementById('neighborhood').value,
            zipCode: document.getElementById('zipCode').value,
            city: document.getElementById('city').value,
            state: document.getElementById('state').value,
            complement: document.getElementById('complement').value,
            number: document.getElementById('number').value
        }
    };

    const token = sessionStorage.getItem('jwtToken');
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    fetch('/patients', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(data)
    })
    .then(function(response) {
        if (response.ok) {
            document.getElementById('successMsg').style.display = 'block';
            document.getElementById('errorMsg').style.display = 'none';
            document.getElementById('patientForm').reset();
        } else {
            return response.json().then(function(err) {
                throw err;
            });
        }
    })
    .catch(function(error) {
        document.getElementById('successMsg').style.display = 'none';
        var errorMsg = document.getElementById('errorMsg');
        if (Array.isArray(error)) {
            errorMsg.textContent = error.map(function(e) { return e.field + ': ' + e.message; }).join(', ');
        } else {
            errorMsg.textContent = error.message || 'Erro ao cadastrar paciente.';
        }
        errorMsg.style.display = 'block';
    });
});
