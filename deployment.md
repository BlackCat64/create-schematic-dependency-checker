## How to deploy on Google Cloud Run and GitHub Pages

Front-end URL: https://blackcat64.github.io/create-schematic-dependency-checker/
Backend URL: https://schematic-dependency-checker-956428592161.europe-west1.run.app/

Whenever changes are made:

1. Ensure the API URL is set correctly in `upload.js`

2. Build the maven project - copies static files to the /docs directory
```bash
./mvnw clean package
```

3. Run a `git push` to deploy the front-end on GitHub Pages

4. Submit the latest build
```bash
gcloud builds submit --tag europe-west1-docker.pkg.dev/schematic-dependency-checker/schematic-repo/schematic-dependency-checker
```

5. Deploy the backend on Google Cloud Run
```bash
gcloud run deploy schematic-dependency-checker \
  --image europe-west1-docker.pkg.dev/schematic-dependency-checker/schematic-repo/schematic-dependency-checker \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated
```

**To view logs**, go to the Logs tab of the `schematic-dependency-checker` Service.
https://console.cloud.google.com/run/detail/europe-west1/schematic-dependency-checker/observability/logs