# 🚗 Vehicle Management API

A **Spring Boot 3** REST API for managing vehicles, garages, and accessories.

---

## 📋 Description

This project is a demo application that exposes a RESTful API to manage:

- **Garages** – create, update, and delete garages with a vehicle capacity
- **Vehicles** – add vehicles to garages, update or remove them
- **Accessories** – attach accessories to vehicles (GPS, camera, etc.)

Business rules are enforced at the service layer (e.g. garage capacity check, vehicle existence validation before saving an accessory).

---

## 🛠️ Tech Stack

| Layer         | Technology                          |
|---------------|-------------------------------------|
| Language      | Java 17                             |
| Framework     | Spring Boot 3.2.5                   |
| Persistence   | Spring Data JPA + H2 (embedded)     |
| Mapping       | MapStruct 1.6.3                     |
| Boilerplate   | Lombok                              |
| Testing       | JUnit 5, Mockito, Testcontainers    |
| Build         | Maven                               |

---

## 📡 API Endpoints

### Garages
| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| GET    | `/api/v1/garages`         | Get all garages       |
| GET    | `/api/v1/garages/{id}`    | Get garage by ID      |
| POST   | `/api/v1/garages`         | Create a garage       |
| PUT    | `/api/v1/garages/{id}`    | Update a garage       |
| DELETE | `/api/v1/garages/{id}`    | Delete a garage       |

### Vehicles
| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| GET    | `/api/v1/vehicles`        | Get all vehicles      |
| GET    | `/api/v1/vehicles/{id}`   | Get vehicle by ID     |
| POST   | `/api/v1/vehicles`        | Create a vehicle      |
| PUT    | `/api/v1/vehicles/{id}`   | Update a vehicle      |
| DELETE | `/api/v1/vehicles/{id}`   | Delete a vehicle      |

### Accessories
| Method | Endpoint                     | Description                  |
|--------|------------------------------|------------------------------|
| GET    | `/api/v1/accessories`        | Get all accessories          |
| GET    | `/api/v1/accessories/{id}`   | Get accessory by ID          |
| POST   | `/api/v1/accessories`        | Add accessory to a vehicle   |
| PUT    | `/api/v1/accessories/{id}`   | Update an accessory          |
| DELETE | `/api/v1/accessories/{id}`   | Delete an accessory          |

---
## 📝 License

This project is for demonstration purposes only.

