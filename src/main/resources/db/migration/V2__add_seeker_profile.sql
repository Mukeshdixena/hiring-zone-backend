CREATE TABLE seeker_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id),
    title VARCHAR(255),
    bio TEXT,
    phone VARCHAR(255),
    location VARCHAR(255),
    portfolio_url VARCHAR(255),
    github_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    skills TEXT
);

CREATE TABLE seeker_experiences (
    id SERIAL PRIMARY KEY,
    profile_id INTEGER REFERENCES seeker_profiles(id),
    company VARCHAR(255),
    position VARCHAR(255),
    start_date DATE,
    end_date DATE,
    current BOOLEAN DEFAULT FALSE,
    description TEXT
);

CREATE TABLE seeker_educations (
    id SERIAL PRIMARY KEY,
    profile_id INTEGER REFERENCES seeker_profiles(id),
    institution VARCHAR(255),
    degree VARCHAR(255),
    field_of_study VARCHAR(255),
    start_year VARCHAR(255),
    end_year VARCHAR(255)
);
