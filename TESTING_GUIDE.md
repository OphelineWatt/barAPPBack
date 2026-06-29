# Guide JUnit et Tests avec Spring Boot

## 📋 Vue d'ensemble

Ce guide vous montre comment écrire et exécuter des tests avec **JUnit 5**, **Mockito** et **Spring Boot Test** pour votre application barapp-backend.

## 🏗️ Architecture des Tests

### 1. **Tests Unitaires (UserServiceTest.java)**
- Testent la logique métier isolée
- Utilisent **Mockito** pour mocker les dépendances
- Pas de base de données, très rapides
- Focalisés sur les cas positifs et les erreurs

**Caractéristiques :**
```java
@Mock - Crée des faux objets (mocks)
@InjectMocks - Injecte les mocks dans le service
when(...).thenReturn(...) - Configure le comportement des mocks
verify(...) - Vérifie que les mocks ont été appelés
```

### 2. **Tests d'Intégration Controller (UserControllerTest.java)**
- Testent les endpoints REST sans toucher la BD
- Utilisent **MockMvc** pour simuler les requêtes HTTP
- Mockent le service
- Valident les réponses HTTP

**Caractéristiques :**
```java
@WebMvcTest(UserController.class) - Charge uniquement le controller
mockMvc.perform(get("/api/users")) - Simule une requête HTTP
.andExpect(status().isOk()) - Valide la réponse
jsonPath("$[0].email", is("test@example.com")) - Valide le JSON
```

### 3. **Tests d'Intégration avec Base de Données (UserServiceIntegrationTest.java)**
- Testent le système complet end-to-end
- Utilisent **TestContainers** avec une vraie PostgreSQL
- Testent les interactions avec la BD réelle
- Plus lents mais très réalistes

**Caractéristiques :**
```java
@Testcontainers - Active les containers
@Container - Définit le container PostgreSQL
@DynamicPropertySource - Configure la connexion DB dynamiquement
```

### 4. **Tests Repository (UserRepositoryTest.java)**
- Testent l'accès aux données JPA
- Utilisent **@DataJpaTest** pour un contexte léger
- Testent les requêtes personnalisées
- Valident les contraintes de base de données

**Caractéristiques :**
```java
@DataJpaTest - Contexte JPA minimaliste
Vérifie la persistence des données
Teste les contraintes uniques, les validations
```

## 🚀 Exécution des Tests

### Exécuter TOUS les tests
```bash
mvn test
```

### Exécuter une classe de test spécifique
```bash
mvn test -Dtest=UserServiceTest
mvn test -Dtest=UserControllerTest
mvn test -Dtest=UserServiceIntegrationTest
mvn test -Dtest=UserRepositoryTest
```

### Exécuter une méthode de test spécifique
```bash
mvn test -Dtest=UserServiceTest#testFindAll
mvn test -Dtest=UserControllerTest#testGetUserSuccess
```

### Exécuter avec rapport de couverture
```bash
mvn clean test jacoco:report
# Le rapport se trouve dans : target/site/jacoco/index.html
```

### Exécuter et afficher les résultats en détail
```bash
mvn test -X
```

## 📝 Structure Générale d'un Test

### Modèle AAA (Arrange-Act-Assert)

```java
@Test
@DisplayName("Description claire du test")
void testSomething() {
    // Arrange - Préparer les données et les mocks
    UserCreateRequest request = ...;
    when(userRepository.save(any())).thenReturn(savedUser);

    // Act - Exécuter l'action à tester
    UserResponse result = userService.create(request);

    // Assert - Vérifier les résultats
    assertEquals("expected@email.com", result.getEmail());
    verify(userRepository, times(1)).save(any());
}
```

## 🎯 Annotations JUnit 5 Principales

| Annotation | Description |
|-----------|-------------|
| `@Test` | Marque une méthode comme test |
| `@DisplayName("...")` | Nom lisible du test |
| `@BeforeEach` | Exécuté avant chaque test |
| `@AfterEach` | Exécuté après chaque test |
| `@BeforeAll` | Exécuté une fois avant tous les tests |
| `@AfterAll` | Exécuté une fois après tous les tests |
| `@Disabled` | Désactive un test |
| `@ParameterizedTest` | Test avec paramètres |

## 🔧 Annotations Mockito

| Annotation | Description |
|-----------|-------------|
| `@Mock` | Crée un mock |
| `@InjectMocks` | Injecte les mocks |
| `@Spy` | Mock partiel (un peu réel) |
| `@Captor` | Capture les arguments passés aux mocks |

## 📊 Assertions Communes

```java
// Égalité
assertEquals(expected, actual);
assertNotEquals(expected, actual);

// Nullité
assertNull(value);
assertNotNull(value);

// Booléens
assertTrue(condition);
assertFalse(condition);

// Exceptions
assertThrows(IllegalArgumentException.class, () -> {
    // code qui doit lever une exception
});

// Collections
assertEquals(2, list.size());
assertTrue(list.contains(item));

// Tous les assertions doivent passer
assertAll(
    () -> assertEquals(1, value1),
    () -> assertEquals(2, value2)
);
```

## 🧪 Assertions avec Hamcrest (MockMvc)

```java
// Dans les test MockMvc
.andExpect(jsonPath("$", hasSize(1)))
.andExpect(jsonPath("$.email", is("test@example.com")))
.andExpect(jsonPath("$.role", notNullValue()))
.andExpect(jsonPath("$.createdAt", instanceOf(Long.class)))
```

## 📦 Dépendances Utilisées

```xml
<!-- JUnit 5 (inclus dans spring-boot-starter-test) -->
junit-jupiter-api
junit-jupiter-engine

<!-- Mockito (inclus dans spring-boot-starter-test) -->
mockito-core

<!-- Spring Boot Test -->
spring-boot-starter-test

<!-- TestContainers pour les tests d'intégration BD -->
testcontainers (version définie dans pom.xml)
testcontainers:junit-jupiter
testcontainers:postgresql
```

## ✨ Bonnes Pratiques

### 1. Testabilité
- ✅ Injecter les dépendances par constructeur
- ❌ Éviter les static
- ✅ Utiliser des interfaces pour les mocks

### 2. Nommage des Tests
```java
// ❌ Mauvais
void test1() { }

// ✅ Bon
@DisplayName("Should return user when id exists")
void testFindByIdSuccess() { }
```

### 3. Isolation
- Chaque test doit être indépendant
- Utiliser `@BeforeEach` pour réinitialiser les données
- Ne pas dépendre de l'ordre des tests

### 4. Couverture
- Tester les chemins heureux ET les erreurs
- Tester les cas limites (null, vide, négatif)
- Tester les validations

### 5. Performance
- ❌ Tests unitaires : < 1 ms
- ❌ Tests MockMvc : < 50 ms
- ⚠️ Tests d'intégration : < 5 secondes

## 🐛 Dépannage

### Les TestContainers ne démarrent pas
```bash
# Vérifier que Docker est actif
docker ps

# Essayer de relancer les tests
mvn clean test
```

### Les mocks ne fonctionnent pas
```java
// ❌ Oublié MockitoAnnotations
class MyTest { }

// ✅ Bon - Initialiser les annotations
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```

### Tests qui prennent trop de temps
```bash
# Utiliser l'isolation pour exécuter que certains tests
mvn test -Dgroups=unit  # Si vous utilisez @Tag("unit")
```

## 📚 Ressources Supplémentaires

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [TestContainers](https://www.testcontainers.org/)

## ✅ Checklist pour Écrire un Bon Test

- [ ] Nom clair et descriptif avec `@DisplayName`
- [ ] Structure AAA (Arrange, Act, Assert)
- [ ] Une seule chose testée par test
- [ ] Données de test réalistes
- [ ] Pas de dépendances entre tests
- [ ] Assertions explicites et claires
- [ ] Vérification des appels des mocks
- [ ] Gestion des exceptions si applicable

---

**Vous êtes maintenant prêt à écrire des tests robustes et maintenables ! 🎉**
