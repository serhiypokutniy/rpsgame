CREATE TABLE IF NOT EXISTS public.player
(
    player_id character varying(100) COLLATE pg_catalog."default" NOT NULL,
    last_updated timestamp with time zone NOT NULL,
    times_played integer NOT NULL DEFAULT 0,
    last_won_score integer NOT NULL DEFAULT 0,
    last_lost_score integer NOT NULL DEFAULT 0,
    CONSTRAINT player_pkey PRIMARY KEY (player_id)
)
