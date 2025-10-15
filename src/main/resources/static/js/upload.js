const form = document.getElementById('uploadForm');
const resultDiv = document.getElementById('result');

form.addEventListener('submit', async (e) => {
    e.preventDefault(); // prevent page reload upon form submit

    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files.length) {
        resultDiv.style.display = "block";
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
        resultDiv.style.display = "block";

        if (!response.ok) {
            resultDiv.innerHTML = `
                        <p class="error">
                            Error ${data.status}: ${data.error || 'Unknown'} - ${data.message || 'Unknown'}
                        </p>
                    `;
            return;
        }

        // build HTML to display list of schematic dependencies
        let dependenciesList = "";

        for (const dep of data.dependencies) {
            let displayed;
            if (dep === 'railways')
                displayed = "Create: Steam n' Rails";
            else displayed = dep;

            dependenciesList += `<li>${displayed}`;
            dependenciesList += `<a href="https://www.curseforge.com/minecraft/search?page=1&pageSize=20&sortBy=relevancy&class=mc-mods&search=${encodeURIComponent(displayed)}" target="_blank">
                                    <img src="/images/curseforge.png" alt="CurseForge" title="Search on CurseForge">
                                 </a>`;
            dependenciesList += `<a href="https://modrinth.com/mods?q=${encodeURIComponent(displayed)}" target="_blank">
                                    <img src="/images/modrinth.png" alt="Modrinth" title="Search on Modrinth">
                                 </a>`;
            dependenciesList += '</li>';
        }

        resultDiv.innerHTML = `
                    <h3>Dependencies for <strong>${data.schematicName}</strong></h3>
                    <ul>${dependenciesList}</ul>
                `;

    } catch (error) {
        console.error(error);
        resultDiv.innerHTML = `<p class="error">An unexpected error occurred.</p>`;
    }
});