# Current Percentage Service

This service listens to the `energy.updates` RabbitMQ queue, reads the matching hour from the `energy_usage` table and updates the `current_percentage` table.

## Start

```bash
mvn spring-boot:run
```

or on Windows:

```cmd
mvn spring-boot:run
```

## Queue

Consumes:

```text
energy.updates
```

## Database tables

Reads:

```text
energy_usage(hour, community_produced, community_used, grid_used)
```

Writes:

```text
current_percentage(hour, community_depleted, grid_portion)
```

## Calculation

```text
community_depleted = community_used / community_produced * 100
grid_portion = grid_used / (community_used + grid_used) * 100
```
