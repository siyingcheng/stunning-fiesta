# stunning-fiesta

An Application based on Spring-Boot

## Configuration

### Configure PostgreSQL

#### Install PostgreSQL

1. Install PostgreSQL, example for archlinux:

    ```shell
    sudo pacman -S postgresql
    ```

2. View PostgreSQL status

    ```shell
    sudo systemctl status postgresql
    ```

3. Switch user `postgres`

   ```shell
   sudo su - postgres
   ```

4. Initialize DB

   ```shell
   initdb --locale en_US.UTF-8 /var/lib/postgres/data
   ```

5. Exit user `postgres`

   ```shell
   exit
   ```

6. Start `postgresql` service

   ```shell
   sudo systemctl start postgresql
   ```
   Then you can check `postgresql` service status use command `sudo systemctl status postgresql`.

7. Add `postgresql` to system startup

   ```shell
   sudo sudo systemctl enable postgresql
   ```
   PostgreSQL should be added to the sytem startup as you can see from below message:

   ```shell
   Created symlink /etc/systemd/system/multi-user.target.wants/postgresql.service →
   /usr/lib/systemd/system/postgresql.service.
   ```

8. Create database, for example: spring_db

   ```shell
   sudo su - postgres
   createdb spring_db
   ```

9. Use a database

   ```shell
   psql spring_db
   ```

#### Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_db
    username: postgres
    password: postgres
```
