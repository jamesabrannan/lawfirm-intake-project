CREATE TABLE IF NOT EXISTS users (
    staff_id TEXT PRIMARY KEY NOT NULL,
    staff_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS clients (
    contact_id TEXT PRIMARY KEY NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL,
    street_address TEXT NOT NULL,
    city TEXT NOT NULL,
    state TEXT NOT NULL,
    postal_code TEXT NOT NULL,
    ssn TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS active_intakes (
    intake_id TEXT PRIMARY KEY NOT NULL,
    contact_id TEXT NOT NULL,
    assigned_paralegal_id TEXT NOT NULL,
    assigned_lawclerk_id TEXT NOT NULL,
    status TEXT NOT NULL,
    summary TEXT NOT NULL,
    document_filenames TEXT NOT NULL,
    expected_document_types TEXT NOT NULL,
    FOREIGN KEY (contact_id) REFERENCES clients (contact_id),
    FOREIGN KEY (assigned_paralegal_id) REFERENCES users (staff_id),
    FOREIGN KEY (assigned_lawclerk_id) REFERENCES users (staff_id)
);

CREATE TABLE IF NOT EXISTS research_memorandums (
    id INTEGER PRIMARY KEY,
    intake_id TEXT NOT NULL UNIQUE,
    research_memorandum_status TEXT
        CHECK (research_memorandum_status IN ('requested', 'completed')),
    filename TEXT,
    FOREIGN KEY (intake_id) REFERENCES active_intakes (intake_id)
);


