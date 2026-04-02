document.getElementById('appointmentForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const data = {
        doctorId: parseInt(document.getElementById('doctorId').value),
        patientId: parseInt(document.getElementById('patientId').value),
        appointmentDate: document.getElementById('appointmentDate').value + ':00'
    };

    const token = sessionStorage.getItem('jwtToken');
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    fetch('/appointments', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(data)
    })
    .then(function(response) {
        if (response.ok) {
            document.getElementById('successMsg').style.display = 'block';
            document.getElementById('errorMsg').style.display = 'none';
            document.getElementById('appointmentForm').reset();
        } else {
            return response.json().then(function(err) {
                throw err;
            });
        }
    })
    .catch(function(error) {
        document.getElementById('successMsg').style.display = 'none';
        var errorMsg = document.getElementById('errorMsg');
        if (error.message) {
            errorMsg.textContent = error.message;
        } else if (Array.isArray(error)) {
            errorMsg.textContent = error.map(function(e) { return e.field + ': ' + e.message; }).join(', ');
        } else {
            errorMsg.textContent = 'Erro ao agendar consulta.';
        }
        errorMsg.style.display = 'block';
    });
});
