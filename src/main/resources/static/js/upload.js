const form = document.getElementById('uploadForm');
const resultDiv = document.getElementById('result');

form.addEventListener('submit', async (e) => {
    e.preventDefault(); // prevent page reload upon form submit

    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files.length) {
        resultDiv.innerHTML = "<p style='color:red'>Please select a file.</p>";
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]); // get the first file in the user's selection

    try {
        const response = await fetch('/api/schematic', { // use REST API to get schematic dependencies
            method: 'POST',
            body: formData
        });

        resultDiv.innerHTML = "<p>Processing file...</p>";

        const data = await response.json();

        if (!response.ok) {
            resultDiv.innerHTML = `
                        <p style='color:red'>
                            Error ${data.status}: ${data.error || 'Unknown'} - ${data.message || 'Unknown'}
                        </p>
                    `;
            return;
        }

        // build HTML to display list of schematic dependencies
        const dependencies = data.dependencies.map(d => `<li>${d}</li>`).join('');
        resultDiv.innerHTML = `
                    <h3>Dependencies for <em>${data.schematicName}</em></h3>
                    <ul>${dependencies}</ul>
                `;

    } catch (error) {
        console.error(error);
        resultDiv.innerHTML = `<p style='color:red'>An unexpected error occurred.</p>`;
    }
});