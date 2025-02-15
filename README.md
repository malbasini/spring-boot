Ripristinare il database MySQL, nel progetto trovi una cartella SQL con il file sql. Carica il file su MySqlWorkbench e crea il database. Aggiorna il tuo utente MySQL e la password del tuo server MySQL nel file HibernateConfig. Modifica il file WebMvcConfig nel metodo addResourceHandlers in modo che il percorso fisico in cui viene fatto l'upload dell'immagine di prodotto sia coerente con il tuo sistema. In sintesi: Scaricare il progetto da GitHub, ripristinare il database, aprire il progetto in IntelliJ IDEA, configurare Tomcat e apportare le modifiche precedentemente descritte al file HibernateConfig e al file WebMvcConfig.

PER CREARE IL CLIENT_ID E LA SECRET_KEY DI PAYPAL SEGUIRE QUESTE ISTRUZIONI:

Accedi alla dashboard di paypal al seguente indirizzo: https://developer.paypal.com/home/ Crea una nuova app con utente businesss (il venditore) copia il Client ID e la Secret nel file application.properties. Assicurati che la modalità sia sandbox. Dopo aver creato un utente business per l'app devi creare un utente client (colui che acquista il corso) seleziona la tua app, vai sul menù Testing Tools e crea un account personal. Questo account ti permetterà di acquistare i corsi.

PER CREARE LA SECRET_KEY DI STRIPE SEGUIRE QUESTE ISTRUZIONI:

Accedi alla dashboard di stripe al seguente indirizzo, registrati se necessario: https://dashboard.stripe.com/test/apikeys ottieni la tua chiave privata e copiala nel file application.properties.

IMPLEMENTARE IL RECAPTCHA DI GOOGLE

Accedi a Google reCAPTCHA Admin Console. Registra un nuovo sito, assicurati che la versione sia V2. Come dominio inserisci localhost, copia il sitekey e il secretkey dentro il file application.properties.
