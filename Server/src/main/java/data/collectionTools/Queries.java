package data.collectionTools;

public class Queries {
    public static final String start = """
                        SELECT b.band_id, b.band_name, b.genre, b.x_coordinate, b.y_coordinate, b.studio_name, b.nop, b.creation_date, b.user_id
                        FROM bands b
                        ORDER BY b.band_id;
                        """;

    public static final String checkTableExists = """
            SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ?);
            """;

    public static final String createBandsTable = """
            create table bands
              (
              	band_id serial primary key,
              	band_name varchar(512) not null,
              	genre varchar(128) not null,
              	x_coordinate double precision not null,
              	y_coordinate double precision not null,
              	studio_name varchar(512),
              	nop integer not null,
              	creation_date varchar(128),
              	user_id integer not_null
              );
            """;

    public static final String createUsersTable = """
            create table users
              (
              	user_id serial primary key,
              	full_name varchar(256) not null,
              	login varchar(256) not null,
              	password bytea not null,
              	salt bytea not null
              );
            """;

    public static final String insertCoordinates = """
                        INSERT INTO coordinates (x, y)
                                VALUES (?, ?);
                        """;

    public static final String addBand = """
            insert into bands (Band_Name, Genre, X_Coordinate, Y_Coordinate, Studio_Name, NOP, Creation_Date, User_ID)
                    values (?, ?, ?, ?, ?, ?, ?, ?);
            """;

    public static final String updateBand = """
                        UPDATE bands
                        SET Band_Name = ?, Genre = ?, X_Coordinate = ?, Y_Coordinate = ?, Studio_Name = ?, NOP = ?, Creation_Date = ?, User_ID = ?
                        WHERE band_id = ?;
                        """;

    public static final String removeBand = """
                        DELETE FROM bands
                        WHERE band_id = ?;
                        """;
    public static final String removeBandsOfUser = """
                        DELETE FROM bands
                        WHERE user_id = ?;
                        """;

    public static final String reorderBands = """
                        SELECT *
                        FROM bands
                        ORDER BY band_id DESC;
                        """;

    public static final String current = """
                        SELECT currval(?);
                        """;

    public static final String maxID = """
                        select max(band_id) from bands;
                        """;

    public static final String isExists = """
                        SELECT band_id
                        FROM bands
                        WHERE band_id = ?;
                        """;
}


