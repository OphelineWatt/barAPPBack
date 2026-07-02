# Bar'app — Backend (API REST)

API REST du projet **Bar'app** : une application de bar à cocktails où les clients
commandent depuis une carte et suivent la préparation, pendant que les barmakers
gèrent la carte et traitent les commandes.

## Technologies
- **Java 21** / **Spring Boot 3.3**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (production / Docker) — **H2** en mémoire (développement)
- **Spring Security + JWT** (jjwt) et **BCrypt** pour les mots de passe
- **MapStruct** (mapping entités ↔ DTO), **Lombok**
- **JUnit 5** + **JaCoCo** (couverture), **Testcontainers**
- **Swagger / OpenAPI** (documentation des routes)

## Prérequis
- Java 21
- Docker + Docker Compose (pour le lancement conteneurisé)
- (le wrapper Maven `mvnw` est inclus, pas besoin d'installer Maven)

## Lancer l'application

### Option A — Docker (base + API + frontend)
> Nécessite que le dossier `barapp-frontend` soit **à côté** de `barapp-backend`
> (le service `frontend` a pour contexte de build `../barapp-frontend`).
```sh
docker compose up --build
```
- **Frontend** (interface) : **http://localhost:8080**
- **API** : **http://localhost:8081**
- **PostgreSQL** : port **5432**
- La base est initialisée automatiquement via `src/main/resources/data.sql`
  (tailles, catégories, cocktails, prix, ingrédients + comptes de démo).

### Option B — En local (base H2 en mémoire)
```sh
./mvnw spring-boot:run
```
- API sur **http://localhost:8081**, base H2 recréée à chaque démarrage.

## Comptes de démonstration
| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Barmaker | `barmaker@barapp.fr` | `barmaker123` |
| Client   | `client@barapp.fr`   | `client123`  |

## Tests & couverture
```sh
./mvnw test
```
Le rapport de couverture JaCoCo est généré dans
`target/site/jacoco/index.html` (couverture instructions ≈ 90 %).

## Documentation des routes
Swagger UI : **http://localhost:8081/swagger-ui/index.html**

### Principales routes
| Méthode | Route | Accès |
|--------|-------|-------|
| POST | `/api/auth/login` | public |
| POST | `/api/users` | public (crée un **client**) |
| POST | `/api/users/barmakers` | BARMAKER (crée un **barmaker**) |
| GET/PUT/DELETE | `/api/users/{id}` | authentifié |
| GET | `/api/cocktails`, `/api/cocktails/{id}` | public |
| POST/PUT/DELETE | `/api/cocktails` | BARMAKER |
| GET | `/api/categories`, `/api/ingredients` | public |
| POST/PUT/DELETE | `/api/categories`, `/api/ingredients` | BARMAKER |
| POST | `/api/orders` | client (authentifié) |
| GET | `/api/orders` | BARMAKER (file des commandes) |
| GET | `/api/orders/user/{userId}` | authentifié |
| POST | `/api/orders/{orderId}/advance-item/{itemId}` | BARMAKER |
| GET | `/api/external/cocktails/search?name=` | BARMAKER (TheCocktailDB) |

L'authentification se fait par **JWT** : après `login`, envoyer le token dans
l'en-tête `Authorization: Bearer <token>`.

## Architecture (paquets)
```
fr.foreach.barapp
├── controller   # points d'entrée REST
├── services     # logique métier
├── repositories # accès base (Spring Data JPA)
├── entities     # entités JPA
├── dtos         # objets de transfert (entrée/sortie API)
├── mapper       # MapStruct (entités ↔ DTO)
├── security     # filtre JWT, UserDetails, JwtService
├── config       # configuration Spring Security + CORS
├── client       # appel externe TheCocktailDB
└── exceptions   # gestion globale des erreurs
```

## Règles métier principales
- Un cocktail a des **ingrédients**, des **tailles** (S/M/L) et un **prix par taille**.
- Une commande calcule automatiquement son **total**.
- Chaque cocktail d'une commande avance par étapes :
  **Préparation des ingrédients → Assemblage → Dressage → Terminée**.
- Quand tous les cocktails sont **Terminée**, la commande passe **Terminée**.
