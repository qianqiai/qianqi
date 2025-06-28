// 自己组装
let uploadButton = document.querySelector('#uploadBtn');
uploadButton.addEventListener('click', () => {

    let form = new FormData();
    // form.append('name', document.querySelector('.formName').value);
    form.append('file', document.querySelector('#upfile').files[0]);
    fetch('/uploadimg', {
        method: 'POST',
        body: form
    }).then(response => response.json()).then((result) => {
        console.log(result);
        alert("恭喜！文件已经处理完成");
    });
});