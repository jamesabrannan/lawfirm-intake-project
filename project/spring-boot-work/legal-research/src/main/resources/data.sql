INSERT INTO users (staff_id, staff_name, email, password, role) VALUES
    ('S-003', 'Jordan Ellis', 'jordan.ellis@example.com', 'password', 'MEMORANDUM_PREPARER'),
    ('S-004', 'Riley Morgan', 'riley.morgan@example.com', 'password', 'MEMORANDUM_PREPARER'),
    ('S-001', 'Maya Chen', 'maya.chen@example.com', 'password', 'INTAKE_PREPARER'),
    ('S-002', 'Doug Smith', 'doug_smith@example.com', 'password', 'INTAKE_PREPARER')
ON CONFLICT (staff_id) DO NOTHING;

INSERT INTO clients (
    contact_id,
    first_name,
    last_name,
    phone,
    email,
    street_address,
    city,
    state,
    postal_code,
    ssn
) VALUES
    ('C-101', 'Elena', 'Rivera', '202-555-0101', 'elena.rivera@example.com', '100 Sample Lane', 'Example City', 'MD', '00000', 'DEMO-SSN-101'),
    ('C-102', 'Mateo', 'Santos', '202-555-0102', 'mateo.santos@example.com', '200 Sample Lane', 'Example City', 'MD', '00000', 'DEMO-SSN-102')
ON CONFLICT (contact_id) DO NOTHING;

INSERT INTO active_intakes (
    intake_id,
    contact_id,
    assigned_paralegal_id,
    assigned_lawclerk_id,
    status,
    summary,
    document_filenames,
    expected_document_types
) VALUES
    ('I-204', 'C-101', 'S-004', 'S-001', 'Ready for review', 'Client requests help after receiving immigration court paperwork about an alleged overstay. She provided a Notice to Appear and a Notice of Hearing. The hearing notice lists December 15, 2026, at 9:00 a.m. Eastern Time. Documents are on file for review.', 'I-204-notice-to-appear.pdf|I-204-notice-of-hearing.pdf', 'notice-to-appear|notice-of-hearing'),
    ('I-205', 'C-102', 'S-004', 'S-002', 'Ready for review', 'Client requests help understanding a USCIS receipt notice for his work-permit application. He also provided a Maryland reckless-driving citation and trial notice. The trial notice lists December 15, 2026, at 9:00 a.m. Eastern Time. Client reports that he has diabetes and experienced sudden confusion around the time of the driving incident. He disputes the officer’s account and says medical records and glucose-monitor data may be available. Those records have not been provided. Documents and client statements are ready for review.', 'I-205-uscis-receipt-notice.pdf|I-205-maryland-uniform-traffic-citation.pdf|I-205-maryland-district-court-trial-notice.pdf', 'notice-to-appear|notice-of-hearing|uscis-receipt-notice|maryland-uniform-traffic-citation|maryland-district-court-trial-notice')
ON CONFLICT (intake_id) DO NOTHING;

INSERT INTO research_memorandums (id, intake_id, research_memorandum_status, filename) VALUES
    (1, 'I-204', 'requested', NULL),
    (2, 'I-205', 'requested', NULL)
ON CONFLICT (id) DO NOTHING;


