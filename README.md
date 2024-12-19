# BookHub

**BookHub** to aplikacja internetowa umożliwiająca użytkownikom przeglądanie, recenzowanie oraz zarządzanie książkami. Aplikacja posiada również panel administratora do zarządzania użytkownikami, książkami oraz recenzjami.

## Funkcjonalności

### Dla niezalogowanych użytkowników:
- Przeglądanie strony głównej z zachętą do rejestracji lub logowania.
- Dostęp do formularza rejestracji i logowania.

### Dla zalogowanych użytkowników:
- Przeglądanie listy książek z możliwością filtrowania, sortowania i paginacji.
- Tworzenie recenzji książek i ocenianie.
- Zarządzanie swoim profilem.

### Panel administratora:
- Przeglądanie listy użytkowników, książek i recenzji.
- Dodawanie, edytowanie i usuwanie pozycji z listy książek.
- Zarządzanie użytkownikami (np. usuwanie kont).
- Zarządzanie recenzjami (np. moderowanie treści).

## Technologie

- **Backend**: Spring Boot
- **Frontend**: Thymeleaf z wykorzystaniem Bootstrap 5
- **Baza danych**: H2 (w trybie testowym)
- **Zarządzanie bezpieczeństwem**: Spring Security

## Wymagania

- JDK 21 lub wyższy
- Maven 3.x
- Przeglądarka internetowa

## Testowe dane w bazie

Aplikacja zawiera kilka wstępnie załadowanych książek i użytkowników, aby umożliwić łatwe testowanie funkcjonalności.
