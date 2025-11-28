/* Register Page JavaScript */
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

// Initialize with student selected
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.role-option').classList.add('selected');
});
