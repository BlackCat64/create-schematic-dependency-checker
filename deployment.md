## How to deploy on Google Cloud Run

URL: https://schematic-dependency-checker-956428592161.europe-west1.run.app/

Whenever changes are made:

1. Submit the latest build
```bash
gcloud builds submit --tag europe-west1-docker.pkg.dev/schematic-dependency-checker/schematic-repo/schematic-dependency-checker
```

2. Deploy the app
```bash
gcloud run deploy schematic-dependency-checker \
  --image europe-west1-docker.pkg.dev/schematic-dependency-checker/schematic-repo/schematic-dependency-checker \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated
```

**To view logs**, go to the Logs tab of the `schematic-dependency-checker` Service.
https://console.cloud.google.com/run/detail/europe-west1/schematic-dependency-checker/observability/logs