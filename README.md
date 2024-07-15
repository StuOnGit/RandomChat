# RandomChat
Chat Random, Server Unix in C, Client Android in Java
# Requisiti funzionali
I requisiti funzionali richiesti sono elencati di seguito:
* Permettere all’utente di sapere quanti clients sono connessi in ogni stanza.
* Permettere all’utente di mettersi in attesa di una chat in determinata stanza.
* Una volta stabilito il match permettere all’utente di scambiare messaggi con l’utente
assegnato e di chiudere la conversazione in qualsiasi momento.
* Non essere assegnato ad una medesima controparte nella stessa stanza in due assegnazioni
consecutive.
* Permettere l’invio di messaggi tramite i servizi di speech recognition del client android.
# Requisiti non funzionali
Il server va realizzato in linguaggio C su piattaforma UNIX/Linux e deve essere ospitato online
su Microsoft Azure. Il client va realizzato in linguaggio Java su piattaforma Android e fa
utilizzo dei servizi di speech recognition. Client e server devono comunicare tramite socket
TCP o UDP. Oltre alle system call UNIX, il server pu`o utilizzare solo la libreria standard del
C. Il server deve essere di tipo concorrente, ed in grado di gestire un numero arbitrario di client
contemporaneamente. Il server effettua il log delle principali operazioni (nuove connessioni,
sconnessioni, richieste da parte dei client) su standard output.
# Progettazione
Per una corretta visualizzazione dei diagrammi in fase di progettazione,
visionare il link: [Diagrammi](Documentazione/Diagrammi/).

# Guida alla compilazione
Per quanto riguarda il `client Android` è possibile visionare nelle rispettive cartelle il gradle e la versione utilizzata per la compilazione.
Invece per il `server C` è possibile sfruttare il comando `./run` che avvierà lo script di compilazione, ovviamente è necessario utilizzare indirizzo IP diversi da quelli segnati, che sono fittizi e riguardanti una macchina virtuale su `Azure` utilizzata per il progetto universitario.

# Idee Progettuali e applicazione
Si decide quindi di iniziare il progetto dalla parte server, tralasciando inizialmente il client
Android. Per testare lo scambio di messaggi, si decide invece di utilizzare il programma telnet
e di utilizzare come area di implementazione la WSL di Windows con connessione locale. Il
protocollo per lo scambio dei messaggi definisce il primo carattere ricevuto come Command
(Comando) e i successivi come Message (Messaggio), in caso di comando sconosciuto o non
valido, il server chiude la connessione con il client. I comandi utilizzati sono tutti char, tranne
per alcune eccezioni, e sono mostrati di seguito:
## COMANDI
1. ENTER ROOM = 69 // `E`
2. NICKNAME = 78 // `N`
3. EXIT = 88 // `X`
4. MSG = 77 // `M`
5. PUSH PUSH LIST // `R`
6. STOP SEARCH = 83 // `S`
7. QUIT ROOM = 81 // `Q`

Il messaggio inviato puo avere grandezza massima `400 byte`. Le stanze verranno invece
create tramite uno scanner che leggerà da un file di testo i titoli delle stanze da creare, separate
da un separatore di righe (in ASCII TABLE = 10) e passate a un vettore creato appositamente.
Verranno utilizzate più strutture dati o struct, per la corretta implementazione del software da
parte del server, quali Queue, RoomVector, User, Link, ognuna delle quali svolge un diverso
compito. Si decide di utilizzare una connessione socket TCP, utilizzando le varie funzioni della
libreria standard del C, e l’utilizzo anche di segnali per la comunicazione fra diversi thread; la
decisione di utilizzare segnali è stata presa in merito alla loro facilità di utilizzo e la buona
utilità che possono dare se utilizzati correttamente.
Ritornando invece alla parte client si decide invece di suddividere l’applicazione in due Activity
principali, l’Activity denominata Main, in cui si effettua la scelta delle stanza, e nella quale è
possibile visionare il numero di utenti in ogni stanza; e l’Activity denominata `Chat` in cui avviene il vero e proprio scambio di messaggi. Si pensa di utilizzare anche un’Activity precedenti
a quella principale (Main) in cui sarà possibile ”catturare” (catch) il Nickname dell’utente, così
da poter effettuare vari controlli sul testo e confermare la sua correttezza, per poi passarlo alle
Activity successive, dando inizio alla RandomChat!

# Guida all'uso
L’applicativo Android ha una prima schermata iniziale nella quale `e possibile inserire un
nickname, un nome identificativo per l’intera durata della sessione all’interno dell’app.

<img src="/Documentazione/Immagini/start_activity.jpg" style="width:240px; height:430px" >

Successivamente, dopo aver inserito un nickname valido, si accede direttamente alla Room
Activity, dove sar`a possibile scegliere fra una delle stanze tematiche e poter vedere quanti utenti
sono connesse a tali stanze.

<img src="/Documentazione/Immagini/room_activity.jpg" style="width:240px; height:460px" >

Una volta scelta una stanza, si ricerca un altro utente con cui chattare.

<img src="/Documentazione/Immagini/search_activity.jpg" style="width:240px; height:460px" >

Quando viene matchato un altro utente inizia la chat vera e propria, in cui sar`a possibile
inviare messaggi di testo vicendevolmente.

<img src="/Documentazione/Immagini/register_activity.jpg" style="width:240px; height:460px" >

Sarà possibile inviare messaggi vocali tramite un long click sull’icona del microfono, al
rilascio inizierà a registrare, fin quando non verr`a stoppato da un altro click dell’utente.
