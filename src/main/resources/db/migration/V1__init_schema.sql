CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    suspended BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id),
    phone VARCHAR(50),
    bio TEXT,
    skills TEXT,
    experience_summary TEXT,
    resume_path VARCHAR(255)
);

CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    description TEXT,
    website VARCHAR(255),
    location VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    company_id INTEGER REFERENCES companies(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    benefits TEXT,
    location VARCHAR(255),
    type VARCHAR(50), -- Full-time, Part-time, etc.
    experience_level VARCHAR(50),
    category VARCHAR(100),
    salary_min NUMERIC(12, 2),
    salary_max NUMERIC(12, 2),
    remote BOOLEAN DEFAULT FALSE,
    flagged BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    deadline TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    job_id INTEGER REFERENCES jobs(id),
    user_id INTEGER REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'Pending', -- Pending, Reviewed, Shortlisted, Rejected, Hired
    cover_letter TEXT,
    resume_path VARCHAR(255), -- For guest apply, or override profile
    guest_name VARCHAR(255),
    guest_email VARCHAR(255),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE saved_jobs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    job_id INTEGER REFERENCES jobs(id),
    UNIQUE(user_id, job_id)
);

CREATE TABLE announcements (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    target VARCHAR(50) NOT NULL, -- ALL, SEEKERS, EMPLOYERS, ADMINS
    type VARCHAR(50) DEFAULT 'info', -- info, warning, success
    created_by_id INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin_logs (
    id SERIAL PRIMARY KEY,
    admin_id INTEGER REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
