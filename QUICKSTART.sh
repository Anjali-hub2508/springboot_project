#!/bin/bash
# Quick Start Script for Book Management System

echo "=========================================="
echo "Book Management System - Quick Start"
echo "=========================================="
echo ""

echo "Step 1: Starting PostgreSQL..."
echo "Make sure PostgreSQL service is running:"
echo "  Windows: Start 'postgresql-x64-15' from Services"
echo "  Or run: net start postgresql-x64-15"
echo ""

echo "Step 2: Creating database (if not exists)..."
psql -U postgres -c "CREATE DATABASE IF NOT EXISTS bookdb;" 2>/dev/null || echo "  ⚠️  Could not create database. Make sure PostgreSQL is running."
echo ""

echo "Step 3: Verifying database..."
psql -U postgres -lqt | grep -w bookdb > /dev/null && echo "  ✅ Database 'bookdb' exists" || echo "  ❌ Database 'bookdb' not found"
echo ""

echo "Step 4: Building and running Spring Boot..."
echo "  Command: mvn clean install && mvn spring-boot:run"
echo ""

echo "=========================================="
echo "Once Spring Boot starts, open your browser:"
echo "  http://localhost:8080/api"
echo "=========================================="
echo ""
echo "Wait for message:"
echo "  'BookManagementApplication started'"
echo "  'Tomcat started on port(s): 8080'"
echo ""
echo "Enjoy! 📚"

