// Interactive features for the Exam System

document.addEventListener('DOMContentLoaded', function() {
    console.log('Adaptive Exam System loaded');

    // Auto-hide success messages after 5 seconds
    const successMessages = document.querySelectorAll('.success-message');
    successMessages.forEach(function(message) {
        setTimeout(function() {
            message.style.transition = 'opacity 0.5s';
            message.style.opacity = '0';
            setTimeout(function() {
                message.remove();
            }, 500);
        }, 5000);
    });

    // Form validation feedback
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(function(field) {
                if (!field.value) {
                    isValid = false;
                    field.style.borderColor = '#dc3545';
                } else {
                    field.style.borderColor = '#28a745';
                }
            });

            if (!isValid) {
                e.preventDefault();
                alert('Please fill in all required fields');
            }
        });
    });

    // Add smooth scrolling
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    // Score input validation
    const scoreInput = document.getElementById('score');
    if (scoreInput) {
        scoreInput.addEventListener('input', function() {
            const value = parseFloat(this.value);
            if (value < 0 || value > 1) {
                this.setCustomValidity('Score must be between 0.0 and 1.0');
            } else {
                this.setCustomValidity('');
            }
        });
    }
});
