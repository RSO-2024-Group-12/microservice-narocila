# Mikrostoritev za Naročila (microservice-narocila)

Ta mikrostoritev je del platforme Nakupify in je odgovorna za upravljanje naročil kupcev. Omogoča ustvarjanje naročil, sledenje njihovemu stanju in pregled zgodovine naročil.

## Tehnološki sklad

- **Ogrodje:** [Quarkus](https://quarkus.io/) (Supersonic Subatomic Java)
- **Podatkovna baza:** PostgreSQL (Hibernate ORM s Panache)
- **Sporočilni sistem:** Apache Kafka (Reactive Messaging)
- **Pakiranje:** Docker, Helm

## Ključne funkcionalnosti

- Ustvarjanje novih naročil.
- Upravljanje statusov naročil (npr. ODDANO, V_OBDELAVI, POSLANO, PREVZETO, PREKLICANO).
- Pregled naročil posameznega uporabnika.
- Integracija s sistemom za pošiljanje preko Kafka sporočil.

## API končne točke

### Javni API (`/api/orders`)

| Metoda | Pot | Opis |
| :--- | :--- | :--- |
| `GET` | `/api/orders` | Pridobi seznam naročil za določenega uporabnika (`userId`). |
| `GET` | `/api/orders/{id}` | Pridobi podrobnosti posameznega naročila po ID-ju. |

### Interni API (`/internal/orders`)

| Metoda | Pot | Opis |
| :--- | :--- | :--- |
| `POST` | `/internal/orders` | Ustvari novo naročilo. |
| `PATCH` | `/internal/orders/{id}/status` | Posodobi status naročila. |

## Razvoj in zagon

### Lokalni zagon v razvojnem načinu

Za zagon aplikacije s podporo za "vroče" ponovno nalaganje kode (live coding) uporabite:

```shell script
./mvnw quarkus:dev
```

Aplikacija bo privzeto dostopna na `http://localhost:8080`. Razvojni vmesnik (Dev UI) je na voljo na `http://localhost:8080/q/dev/`.

### Pakiranje aplikacije

Za pakiranje aplikacije v JAR datoteko:

```shell script
./mvnw package
```

Za izdelavo *über-jar* (vsebuje vse odvisnosti):

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

### Izgradnja Docker slike

Aplikacijo lahko zapakirate v Docker sliko z ukazom:

```shell script
docker build -t nakupify/microservice-narocila .
```

## Konfiguracija

Konfiguracijski parametri se nahajajo v `src/main/resources/application.properties`. Glavne nastavitve vključujejo:

- `quarkus.datasource.jdbc.url`: Povezava do PostgreSQL baze.
- `mp.messaging.outgoing.orders-out.connector`: Nastavitve za povezavo s Kafka.

## Avtomatski testi

Za zagon vseh testov uporabite:

```shell script
./mvnw test
```
