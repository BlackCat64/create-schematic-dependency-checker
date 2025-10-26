const form = document.getElementById('uploadForm');
const resultDiv = document.getElementById('result');
const spinner = document.getElementById("loading-spinner");
const outputDiv = document.getElementById("output");

form.addEventListener('submit', async (e) => {
    e.preventDefault(); // prevent page reload upon form submit

    const fileInput = document.getElementById('fileInput');

    // show error if Upload was clicked without uploading a file
    if (!fileInput.files.length) {
        resultDiv.style.display = "block";
        outputDiv.innerHTML = "<p class='error'>Please select a file.</p>";
        return;
    }

    resultDiv.style.display = "block";
    spinner.style.display = "block";
    outputDiv.innerHTML = ""; // Clear previous results when next results are loading

    let copyButton = document.getElementById("copyButton");
    if (copyButton) {
        copyButton.style.display = "none"; // Hide copy button until result is shown
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]); // get the first file in the user's selection

    try {
        // use backend REST API to get schematic dependencies - hosted on Google Cloud Run
        // const apiURL = '/api/schematic';
        const apiURL = 'https://schematic-dependency-checker-956428592161.europe-west1.run.app/api/schematic';
        const response = await fetch(apiURL, {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        // Hide loading spinner once all data has been received from the backend
        spinner.style.display = "none";

        if (!response.ok) {
            outputDiv.innerHTML = `
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

            dependenciesList += `<tr><td>${displayed}</td>`;
            dependenciesList += `<td><a href="https://www.curseforge.com/minecraft/search?page=1&pageSize=20&sortBy=relevancy&class=mc-mods&search=${encodeURIComponent(displayed)}" target="_blank">
                                    <img src="images/curseforge.png" alt="CurseForge" title="Search on CurseForge">
                                 </a></td>`;
            dependenciesList += `<td><a href="https://modrinth.com/mods?q=${encodeURIComponent(displayed)}" target="_blank">
                                    <img src="images/modrinth.png" alt="Modrinth" title="Search on Modrinth">
                                 </a></td>`;
            dependenciesList += '</tr>';
        }

        outputDiv.innerHTML = `
                    <div class="result-header">
                    <h3>Dependencies for <strong>${data.schematicName}</strong></h3>
                    <button id="copyButton">📋 Copy to Clipboard</button>
                    </div>
                    <table>${dependenciesList}</table>
                    <p class="more-info">
                        Only mod IDs are shown. These are usually very similar to the mod's name, but in some cases they may be very different.
                        <br>
                        For example, Create: Steam n' Rails has the ID 'railways'. On this site, there's a special exception for that one though!
                    </p>
                `;

        copyButton = document.getElementById("copyButton");
        copyButton.style.display = "block";

        copyButton.onclick = async () => {
            const textToCopy = data.dependencies.join("\n");
            try {
                await navigator.clipboard.writeText(textToCopy);
                copyButton.textContent = "Copied!";
                setTimeout(() => copyButton.textContent = "📋 Copy to Clipboard", 2000);
            } catch (err) {
                console.error("Failed to copy text: ", err);
                copyButton.textContent = "Copy Failed";
            }
        };
    }
    catch (error) {
        spinner.style.display = "none";
        console.error(error);
        outputDiv.innerHTML = `<p class="error">An unexpected error occurred: ${error.message}</p>`;
    }
});