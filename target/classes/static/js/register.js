/* Register Page JavaScript */

/**
 * Handle role selection (Student or Teacher)
 * @param {string} role - Either 'STUDENT' or 'TEACHER'
 */
function selectRole(role) {
    // Update radio buttons
    document.getElementById('studentRole').checked = (role === 'STUDENT');
    document.getElementById('teacherRole').checked = (role === 'TEACHER');
    
    // Update visual selection
    document.querySelectorAll('.role-option').forEach(option => {
        option.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');
    
    // Show/hide department field
    const deptGroup = document.getElementById('departmentGroup');
    const deptInput = document.getElementById('department');
    
    if (role === 'TEACHER') {
        deptGroup.style.display = 'block';
        deptInput.required = true;
    } else {
        deptGroup.style.display = 'none';
        deptInput.required = false;
    }
}

// Initialize with student selected on page load
document.addEventListener('DOMContentLoaded', function() {
    const firstRoleOption = document.querySelector('.role-option');
    if (firstRoleOption) {
        firstRoleOption.classList.add('selected');
    }
});
