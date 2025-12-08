-- Add name column to users table
ALTER TABLE users 
ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT 'User' AFTER email;

