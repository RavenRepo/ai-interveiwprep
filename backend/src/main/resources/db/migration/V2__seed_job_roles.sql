-- ═══════════════════════════════════════════════════════════════
-- V2__seed_job_roles.sql
-- Flyway migration: Seed the job_roles lookup table
-- ═══════════════════════════════════════════════════════════════
-- This data was previously in data.sql and executed on every
-- application startup via spring.jpa.defer-datasource-initialization=true.
-- Now managed by Flyway so it runs exactly once.
--
-- INSERT IGNORE is used so this migration is safe to run against
-- databases that already have the seed data from the old data.sql
-- approach. Rows that already exist (by primary key) are silently
-- skipped rather than causing a duplicate key error.
-- ═══════════════════════════════════════════════════════════════

INSERT IGNORE INTO job_roles (id, title, description, category, active) VALUES
(1,  'Software Engineer',          'Develop, test, and maintain software applications',                          'Technology',       TRUE),
(2,  'Data Analyst',               'Analyze and interpret complex data to help organizations make decisions',    'Technology',       TRUE),
(3,  'Product Manager',            'Oversee product development from conception to launch',                      'Business',         TRUE),
(4,  'Business Analyst',           'Identify business needs and find technical solutions',                       'Business',         TRUE),
(5,  'Marketing Manager',          'Plan and execute marketing strategies and campaigns',                        'Marketing & Sales', TRUE),
(6,  'Sales Executive',            'Drive sales growth and manage client relationships',                         'Marketing & Sales', TRUE),
(7,  'HR Manager',                 'Manage recruitment, employee relations, and organizational culture',         'Operations',       TRUE),
(8,  'Financial Analyst',          'Analyze financial data and provide insights for business decisions',         'Business',         TRUE),
(9,  'UI/UX Designer',             'Design user interfaces and enhance user experience',                        'Technology',       TRUE),
(10, 'Project Manager',            'Plan, execute, and close projects while managing resources',                 'Business',         TRUE),
(11, 'Data Scientist',             'Build predictive models and extract insights from data',                     'Technology',       TRUE),
(12, 'DevOps Engineer',            'Manage infrastructure, automation, and deployment pipelines',                'Technology',       TRUE),
(13, 'Customer Success Manager',   'Ensure customer satisfaction and drive product adoption',                    'Marketing & Sales', TRUE),
(14, 'Content Writer',             'Create compelling content for various marketing channels',                   'Marketing & Sales', TRUE),
(15, 'Operations Manager',         'Oversee daily business operations and process optimization',                 'Operations',       TRUE);
